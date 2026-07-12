# Feature breakdown / user stories

## Trainee (Android app)

| Screen | User story |
|---|---|
| Login / Register | As a trainee, I can create an account or sign in with email/password or Google, so my data is tied to one identity across app reinstalls. |
| Onboarding — fitness level | As a new trainee, I pick beginner / intermediate / advanced so my plan matches my capability. |
| Onboarding — equipment | As a new trainee, I select what equipment I actually have (bodyweight only, dumbbells, resistance bands, full gym) so my plan never prescribes something I can't do. |
| Dashboard | As a trainee, I see today's workout summary and my current streak the moment I open the app. |
| Workout plan | As a trainee, I see my full week broken into days, each with exercises, sets, and reps. |
| Workout detail | As a trainee, during a session I see one exercise at a time with a rest timer between sets, and I can mark each set complete. |
| Progress | As a trainee, I see a calendar of which days I worked out and a chart of my training volume over time, so missed days are visible and motivating to fix. |
| Profile | As a trainee, I can change my fitness level or equipment and regenerate my plan, or sign out. |

## Trainer (React dashboard)

| Screen | User story |
|---|---|
| Login | As a trainer, I sign in with the same account system as trainees, and only accounts with the trainer role can reach the dashboard. |
| Roster | As a trainer, I see every trainee assigned to me with their current streak and adherence percentage, so I know at a glance who's falling behind. |
| Trainee detail | As a trainer, I can open one trainee and see their full workout history and current plan. |
| Plan editor | As a trainer, I can override a trainee's auto-generated plan when their program needs a manual adjustment. |

## Non-functional requirements

- **Offline tolerance**: the Android app must render the last-known plan and streak from Firestore's local cache when there's no network.
- **Auth consistency**: a single Firebase project backs both clients; there is exactly one place (`backend-api`) that assigns the `trainer` role.
- **Personalization determinism**: plan generation for the same `(fitnessLevel, equipment, goal, daysPerWeek)` input is reproducible, which makes the generator unit-testable.
