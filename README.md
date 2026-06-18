# J Arena — Football Field Booking System

A full-stack web application for managing football field bookings, teams, tournaments, events, and notifications. Built with Java Spring MVC, Hibernate ORM, Thymeleaf, and MySQL.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 11, Spring MVC 5.3.31 (annotation-based, no XML) |
| ORM | Hibernate 5.6.15 with `SessionFactory` |
| View | Thymeleaf 3 + `thymeleaf-extras-java8time` |
| Database | MySQL 8 (via XAMPP) |
| Frontend | Bootstrap 5, Bootstrap Icons |
| Server | Apache Tomcat 9, port 9090 |
| Build | Maven (WAR packaging) |

---

## How to Run

### Prerequisites
- Java 11 or later
- Apache Maven 3.6+
- XAMPP (for MySQL)
- Apache Tomcat 9 installed at `C:\Tomcat9`

### Step 1 — Start MySQL
Open XAMPP Control Panel and click **Start** next to MySQL.

> **Important:** MySQL must be running before Tomcat starts, otherwise the app fails to initialize.

### Step 2 — Configure the Database
The database connection is in `src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/jarena_db
db.username=root
db.password=
```

Create the database once (in phpMyAdmin or MySQL CLI):
```sql
CREATE DATABASE jarena_db;
```

Hibernate will create all tables automatically on first run (`hbm2ddl.auto=update`).

### Step 3 — Build the WAR
```bash
cd C:\Users\shimaa\Downloads\alpha_dev
mvn clean package
```
This produces `target/alpha_dev.war`.

### Step 4 — Deploy to Tomcat
1. Stop Tomcat (if running)
2. Delete the old deployment folder if it exists: `C:\Tomcat9\webapps\alpha_dev\`
3. Copy the WAR:
   ```
   copy target\alpha_dev.war C:\Tomcat9\webapps\
   ```
4. Start Tomcat — it auto-deploys in approximately 15 seconds

### Step 5 — Open the App
```
http://localhost:9090/alpha_dev/
```

### Default Admin Account
Register a new user via the app, then manually set `role = 'ADMIN'` in the `profiles` table using phpMyAdmin.

---

## Project Structure

```
src/
├── main/
│   ├── java/com/jarena/
│   │   ├── config/          # Spring MVC, Hibernate, and servlet configuration
│   │   ├── controller/      # Spring MVC request handlers
│   │   ├── dao/             # Data Access Objects (Hibernate HQL)
│   │   ├── model/           # Hibernate entity classes
│   │   ├── service/         # Business logic layer
│   │   └── util/            # Helpers (AuthHelper)
│   └── resources/
│       ├── templates/       # Thymeleaf HTML templates
│       └── database.properties
```

---

## Modules

---

### Module 1 — Authentication & User Management

Handles registration, login, logout, and session-based authentication.

| Type | File |
|---|---|
| Model | `model/User.java` |
| DAO | `dao/UserDao.java`, `dao/UserDaoImpl.java` |
| Service | `service/UserService.java`, `service/UserServiceImpl.java` |
| Controller | `controller/AuthController.java` |
| Utility | `util/AuthHelper.java` |
| Templates | `templates/auth/login.html`, `templates/auth/register.html` |

**Key behavior:**
- Users register with full name, email, and password
- Authenticated user is stored in `HttpSession`
- `AuthHelper.isLoggedIn()`, `.isAdmin()`, `.getLoggedInUser()` are called at the top of every controller method
- Any unauthenticated access redirects to `/login`
- Roles: `CUSTOMER` (default) or `ADMIN`

---

### Module 2 — Dashboard

Displays real-time statistics for the logged-in user or admin.

| Type | File |
|---|---|
| Controller | `controller/DashboardController.java` |
| Templates | `templates/user-management/customer-dashboard.html` |

**What it shows:**
- **Customer:** total bookings, upcoming bookings, total amount spent
- **Admin:** total bookings, total revenue, registered users, active fields

---

### Module 3 — Bookings

Core module for reserving football fields.

| Type | File |
|---|---|
| Model | `model/Booking.java` |
| DAO | `dao/BookingDao.java`, `dao/BookingDaoImpl.java` |
| Service | `service/BookingService.java`, `service/BookingServiceImpl.java` |
| Controller | `controller/BookingController.java` |
| Templates | `templates/bookings/customer-bookings.html` |

**Key behavior:**
- Customer selects a field and picks a date and time slot
- Booking is created with status `PENDING`
- Admin can confirm or cancel bookings from the admin panel
- A confirmed and completed booking automatically creates a `Payment` record

---

### Module 4 — Fields Management

Manages the football fields that can be booked.

| Type | File |
|---|---|
| Model | `model/Field.java` |
| DAO | `dao/FieldDao.java`, `dao/FieldDaoImpl.java` |
| Service | `service/FieldService.java`, `service/FieldServiceImpl.java` |
| Controllers | `controller/FieldController.java` (customer), `controller/AdminFieldController.java` (admin) |
| Templates | `templates/fields/customer-fields.html` — field cards grid with Book Now button |
| | `templates/fields/admin-fields.html` — management table with stats |
| | `templates/fields/create-field.html` — add new field form |
| | `templates/fields/edit-field.html` — edit field form |

**Customer URL:** `GET /fields`
**Admin URLs:** `GET /admin/fields`, `GET/POST /admin/fields/create`, `GET/POST /admin/fields/edit/{id}`, `POST /admin/fields/delete/{id}`, `POST /admin/fields/toggle/{id}`

**Key behavior:**
- Admin creates fields with name, location, price per hour, and image URL
- Availability can be toggled on/off per field
- Fields with active bookings cannot be deleted

---

### Module 5 — Payments

Tracks payment records associated with confirmed bookings.

| Type | File |
|---|---|
| Model | `model/Payment.java` |
| DAO | `dao/PaymentDao.java`, `dao/PaymentDaoImpl.java` |
| Service | `service/PaymentService.java`, `service/PaymentServiceImpl.java` |
| Controller | `controller/PaymentController.java` |
| Templates | `templates/payments/customer-payments.html` |

**Customer URL:** `GET /payments`

---

### Module 6 — Notifications

In-app notification system for sending messages to users.

| Type | File |
|---|---|
| Model | `model/Notification.java` |
| DAO | `dao/NotificationDao.java`, `dao/NotificationDaoImpl.java` |
| Service | `service/NotificationService.java`, `service/NotificationServiceImpl.java` |
| Controllers | `controller/NotificationController.java` (customer), `controller/AdminNotificationController.java` (admin) |
| Templates | `templates/notifications/customer-notifications.html` — notification inbox with filter tabs |
| | `templates/notifications/admin-notifications.html` — send panel + all notifications table |

**Customer URL:** `GET /notifications`
**Admin URLs:** `GET /admin/notifications`, `POST /admin/notifications/send`, `POST /admin/notifications/delete/{id}`

**Key behavior:**
- Admin composes a notification (title, message, type) and sends to:
  - **All Users** — one click broadcasts to every registered user
  - **Specific Users** — searchable multi-select dropdown to pick recipients
- Notification types: `GENERAL`, `BOOKING_CONFIRMED`, `PAYMENT_RECEIVED`, `REMINDER`, `CANCELLATION`
- Unread count badge appears on the Notifications link in the sidebar across all pages
- Customers can mark individual notifications as read or delete them

---

### Module 7 — Events (Promotions)

Manages promotional events and announcements.

| Type | File |
|---|---|
| Model | `model/Event.java` |
| DAO | `dao/EventDao.java`, `dao/EventDaoImpl.java` |
| Service | `service/EventService.java`, `service/EventServiceImpl.java` |
| Controllers | `controller/EventController.java` (customer), `controller/AdminEventController.java` (admin) |
| Templates | `templates/events/customer-events.html` — event listing |
| | `templates/events/event-detail.html` — single event detail page |
| | `templates/events/admin-events.html` — admin CRUD |

**Customer URLs:** `GET /events`, `GET /events/{id}`
**Admin URLs:** `GET /admin/events`, create, edit, delete

---

### Module 8 — Teams & Tournaments

Full competitive module covering team management, tournament registration, match scheduling, result tracking, and leaderboard rankings.

---

#### 8a — Teams

| Type | File |
|---|---|
| Models | `model/Team.java` — team with stats (W/L/D/points) |
| | `model/TeamMember.java` — membership join table |
| DAO | `dao/TeamDao.java`, `dao/TeamDaoImpl.java` |
| Service | `service/TeamService.java`, `service/TeamServiceImpl.java` |
| Controller | `controller/TeamController.java` |
| Templates | `templates/teams/customer-teams.html` — team list, join, open tournaments, upcoming matches |
| | `templates/teams/create-team.html` — create team form |
| | `templates/teams/team-detail.html` — members list, match history, leave/delete |
| | `templates/teams/leaderboard.html` — ranked table with gold/silver/bronze medals |

**Customer URLs:**

| URL | Description |
|---|---|
| `GET /teams` | Main teams page |
| `GET /teams/create` | Create team form |
| `GET /teams/{id}` | Team detail (members, history) |
| `POST /teams/join/{id}` | Join a team |
| `POST /teams/leave/{id}` | Leave a team |
| `POST /teams/delete/{id}` | Delete team (captain only) |
| `GET /leaderboard` | Team rankings |

**Business rules:**
- A user can only be in one team at a time
- Team captain is automatically added as a member on creation
- Only the captain can delete the team
- Leaderboard ranks by: points → wins → draws

---

#### 8b — Tournaments

| Type | File |
|---|---|
| Models | `model/Tournament.java` — tournament details and status |
| | `model/TournamentRegistration.java` — team-tournament registration |
| DAO | `dao/TournamentDao.java`, `dao/TournamentDaoImpl.java` |
| Service | `service/TournamentService.java`, `service/TournamentServiceImpl.java` |
| Controller | `controller/AdminTournamentController.java` |
| Templates | `templates/teams/admin-tournaments.html` — tournament list + upcoming matches overview |
| | `templates/teams/create-tournament.html` — new tournament form |
| | `templates/teams/edit-tournament.html` — edit tournament form |
| | `templates/teams/tournament-detail.html` — detail page (shared between customer and admin) |

**Tournament statuses:** `UPCOMING` → `ACTIVE` → `COMPLETED` (or `CANCELLED`)

**Customer URLs:**

| URL | Description |
|---|---|
| `GET /teams/tournaments/{id}` | View tournament, register/unregister |
| `POST /teams/tournaments/register/{id}` | Register your team (captain only) |
| `POST /teams/tournaments/unregister/{id}` | Unregister your team (captain only) |

**Admin URLs:**

| URL | Description |
|---|---|
| `GET /admin/tournaments` | List all tournaments |
| `GET/POST /admin/tournaments/create` | Create new tournament |
| `GET/POST /admin/tournaments/edit/{id}` | Edit tournament |
| `POST /admin/tournaments/delete/{id}` | Delete tournament (cascades to matches & registrations) |
| `POST /admin/tournaments/status/{id}` | Change status |
| `GET /admin/tournaments/detail/{id}` | Tournament detail with match result entry |

**Business rules:**
- Registration only open when status is `UPCOMING`
- Cannot register more teams than `maxTeams`
- Deleting a tournament removes all its matches and registrations

---

#### 8c — Matches

| Type | File |
|---|---|
| Model | `model/Match.java` — home/away teams, field, date, time, scores, status |
| DAO | `dao/MatchDao.java`, `dao/MatchDaoImpl.java` |
| Service | `service/MatchService.java`, `service/MatchServiceImpl.java` |
| Template | `templates/teams/schedule-match.html` — match scheduling form |

**Admin URLs:**

| URL | Description |
|---|---|
| `GET/POST /admin/tournaments/schedule/{tournamentId}` | Schedule a match between two registered teams |
| `POST /admin/tournaments/matches/result/{id}` | Enter final score (sets status to COMPLETED) |
| `POST /admin/tournaments/matches/delete/{id}` | Cancel/delete a match |

**Business rules:**
- A team cannot play against itself
- Entering a result automatically updates team standings:
  - Win → +3 points, +1 win
  - Draw → +1 point each, +1 draw
  - Loss → +1 loss (no points)
- Match statuses: `SCHEDULED` → `COMPLETED` (or `CANCELLED`)

---

## Database Tables

| Table | Module | Description |
|---|---|---|
| `profiles` | Auth | Registered users (id, full_name, email, password, role) |
| `fields` | Fields | Football fields available for booking |
| `bookings` | Bookings | Field reservations |
| `payments` | Payments | Payment records |
| `notifications` | Notifications | In-app messages per user |
| `events` | Events | Promotional events |
| `teams` | Teams | Teams with W/L/D/points stats |
| `team_members` | Teams | Team membership (user ↔ team) |
| `tournaments` | Tournaments | Tournament details and status |
| `tournament_registrations` | Tournaments | Which teams joined which tournament |
| `matches` | Matches | Scheduled and completed match records |

> All tables are created automatically by Hibernate on first startup — no SQL scripts needed.

---

## Configuration Files

| File | Purpose |
|---|---|
| `src/main/resources/database.properties` | Database connection settings |
| `src/main/java/com/jarena/config/WebAppConfig.java` | Spring MVC beans, Thymeleaf resolver, Java 8 time dialect |
| `src/main/java/com/jarena/config/HibernateConfig.java` | SessionFactory, DataSource, TransactionManager |
| `src/main/java/com/jarena/config/WebAppInitializer.java` | Servlet 3.0 bootstrap (replaces `web.xml`) |

---

*J Arena — Built with Spring MVC 5 · Hibernate 5 · Thymeleaf 3 · Bootstrap 5*
