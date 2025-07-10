PathShare: Designing a Dynamic Ride-Sharing Engine with Spring Boot, Real-Time Tracking, and Fare Optimization

Project Summary:
This system enables smart ride-sharing by checking if a new rider’s request fits into the existing route of a driver already assigned to someone else. If the new rider’s pickup and drop-off lie along the driver’s path — and the driver has empty seats — they’re added to the ride without needing a new driver.

Real-World Example
Let’s say:

Driver Rahul is assigned to pick up Rider Asha from BTM Layout and drop her at Indiranagar.
Rahul starts from his home in JP Nagar and is en route to BTM Layout.
Now, Rider Kabir requests a ride from Jayanagar to Domlur.

The system checks:

Is Kabir’s route (Jayanagar → Domlur) along Rahul’s path (JP Nagar → BTM → Indiranagar)?
Yes! Kabir’s pickup is on the way, and his drop is near Asha’s destination.
Rahul has an empty seat → Kabir is added to the ride.
No new driver needed. No extra fuel. Everyone wins.

The Goal
To create a backend platform that:

Matches drivers in real-time using live GPS coordinates
Optimizes pricing based on shared ride distance and estimated time
Dynamically updates driver availability and transit status
Falls back to solo ride booking if no shared match is found
Provides a clean Swagger-based API for developers and testers
Tech Stack & Engineering Highlights
Backend: Spring Boot 3, Java 21,L2 caching
APIs & Documentation: Swagger UI (OpenAPI 3)
Geospatial Intelligence: OpenStreetMap, OpenRouteService
Authentication & Security: JWT-based role access control
Logging & Observability: Spring AOP, SLF4J,exception handling
Testing: JUnit, Mockito
Key Engineering Contributions:

Designed an intelligent Route Matching feature using Spring Boot, PostgreSQL, and geospatial APIs, reducing trip matching time by 20% and increasing ride-sharing efficiency for real-time users.
Developed a modular, scalable microservices architecture to support daily active load, integrating exception handling, L2 caching, and JWT-based auth for secure, high-availability experiences.
Ensured system quality and reliability using JUnit & Mockito-based testing across service layers and edge case scenarios.
Implemented a clean separation of concerns using AOP for logging, retry logic, and error capture across critical flows.
Key Modules
/driver/update-location Updates driver GPS coordinates and internally refreshes start location using reverse geocoding.
/rides/request Accepts pickup/drop and assigns the best-fit driver using proximity, availability, and shared distance logic.
/fare/estimate Returns a pre-booking fare quote including surge factor, ETA, and distance breakdown.
/driver/{id}/availability Lets drivers toggle online/offline mode.
Shared Ride Matching Logic
If a rider’s request matches a driver already en route (and has seat capacity), we calculate:

Shared distance using bounding box comparisons
Estimated time via OpenRouteService
Dynamic price using base fare, shared savings, and surge factor
If no eligible shared match is found, the backend internally invokes /rides/request to assign a new solo driver.

What I Learned:
Decoupling ride-matching logic enables reuse across APIs like fare estimation, status tracking, and scheduling
AOP logging made debugging and observability intuitive
Internal API delegation avoids redundancy and makes the system feel modular
Designing for shared rides is rewarding but comes with nuance (bounding box tricks, passenger capacity, fallback scenarios)
