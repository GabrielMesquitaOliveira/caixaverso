# Regras de Negócio - Microservices

Este documento detalha **exatamente** as regras de negócio das services `user` e `reservation`, sem detalhes de infraestrutura ou bibliotecas, para servir de base na recriação destas services utilizando **Java Quarkus com Clean Architecture**.

---

## 1. User Service

A service de `user` possui um escopo de regras mais focado em um CRUD simples com persistência básica, não contendo validações complexas.

### 1.1 Entidades de Domínio
- **User**:
  - `id` (Identificador único)
  - `username` (Nome de usuário)
  - `email` (Endereço de e-mail)
  - `fullName` (Nome completo)

### 1.2 Casos de Uso (Use Cases)

#### **Criar Usuário (Create User)**
- **Ação**: Recebe os dados de requisição, mapeia para a entidade de domínio `User` e persiste no repositório de dados.
- **Retorno**: Retorna os dados do usuário salvo.

#### **Buscar Usuário (Get User(s))**
- **Busca por ID**: Recupera o usuário através do identificador único (ID).
  - **Exceção**: Se o usuário não for encontrado pelo ID, o sistema deve lançar obrigatoriamente a exceção de negócio `UserNotFoundException`.
- **Busca de todos**: Retorna uma lista com todos os usuários cadastrados.

---

## 2. Reservation Service

A service de `reservation` contém o núcleo e a maior parte da lógica complexa de negócios do sistema de agendamentos.

### 2.1 Entidades de Domínio
- **Reservation**:
  - `id` (Identificador único)
  - `userId` (ID do usuário que fez a reserva)
  - `resourceName` (Nome do recurso sendo reservado - ex: sala, mesa, etc.)
  - `startDate` (Data e hora de início)
  - `endDate` (Data e hora de término)
  - `status` (Situação da reserva)

### 2.2 Validações de Domínio (Business Rules)
As regras de validação para a criação e checagem de reservas são rigorosas:

1. **Duração da Reserva (Duration Rule)**
   - Uma reserva **deve** ter a duração de exatamente **30 minutos** (`SLOT_DURATION_MINUTES = 30`).
   - *Exceção*: Caso contrário, lança `InvalidDurationException`.

2. **Horário de Início (Time Slot Rule)**
   - O minuto inicial da reserva (`startDate`) deve ser obrigatoriamente fechado em `:00` ou `:30`. (ex: 10:00 ou 10:30).
   - *Exceção*: Caso contrário, lança `InvalidTimeSlotException`.

3. **Horário Comercial (Business Hours Rule)**
   - As reservas devem acontecer dentro do horário comercial, definido entre **08:00 (Início)** e **18:00 (Término)**.
   - O horário de início (`startDate`) não pode ser anterior às 08:00.
   - O horário de término (`endDate`) não pode passar das 18:00 em ponto (ex: se terminar às 18:01, é inválido).
   - *Exceção*: Lança `BusinessHoursViolationException` se a regra for violada.

4. **Sobreposição de Reservas (Overlap/Double Booking Rule)**
   - Para um mesmo recurso (`resourceName`), não podem existir duas reservas ocupando o mesmo intervalo de tempo.
   - O cálculo do conflito segue a lógica matemática de interseção: `startDate1 < endDate2` e `endDate1 > startDate2`.
   - *Exceção*: Lança `SlotAlreadyBookedException` em caso de conflito de horários para aquele recurso.

### 2.3 Casos de Uso (Use Cases)

#### **Criar Reserva (Create Reservation)**
1. **Validação de Usuário Externo**: Primeiro, o sistema deve verificar em comunicação cross-service se o `userId` enviado existe na **User Service**. 
2. **Execução das Validações**: Executa sequencialmente as validações de:
   - Duração (exatos 30 min)
   - Horário de Início (minuto 0 ou 30)
   - Horário Comercial (das 8h às 18h)
3. **Checagem de Sobreposição**: Busca as reservas existentes para o mesmo recurso (`resourceName`) no mesmo dia para garantir que não haja choque de horários (passa pela validação de sobreposição).
4. **Criação da Reserva**: Salva a reserva, atribuindo o status inicial como `"CONFIRMED"` (o contrato inicial salva no formato *Confirmed*).
5. **Retorno**: Retorna os dados da reserva incluindo o nome de usuário (`username`) originado da checagem do User Service.

#### **Buscar Horários Disponíveis (Get Available Slots)**
- **Ação**: A partir de uma data específica (`LocalDate`) e um recurso (`resourceName`):
  1. O sistema deve gerar **todos os slots (intervalos) possíveis** de 30 minutos (das 08:00 às 18:00) para aquela data/recurso num estado inicial de "Disponível".
  2. O sistema busca no repositório todas as reservas confirmadas daquele dia e recurso.
  3. O sistema compara os "espaços de tempo gerados" com o as "reservas existentes" no banco.
- **Retorno**: Retorna a lista de todos os slots de tempo gerados, com o uma flag/boleano `isAvailable` (ou similar) setada como `false` nos intervalos exatos que já possuem uma reserva registrada e `true` nos que estão livres.
