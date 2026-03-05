# Patient Management System

Microservices-based patient management platform built with Spring Boot, API Gateway, Kafka, PostgreSQL, Docker, and LocalStack.

## Services

- `api-gateway` (Spring Cloud Gateway)
- `auth-service` (JWT auth)
- `patient-service` (patient CRUD)
- `billing-service` (REST + gRPC)
- `analytics-service` (Kafka consumer/analytics)
- `infrastructure` (AWS CDK stack for LocalStack)
- `integration-tests`

## Tech Stack

- Java 21
- Spring Boot
- Spring Cloud Gateway
- PostgreSQL
- Apache Kafka
- Docker / Docker Compose
- AWS CDK + LocalStack

## Repository Structure

```text
Patient-Management/
  api-gateway/
  auth-service/
  patient-service/
  billing-service/
  analytics-service/
  infrastructure/
  integration-tests/
  api-requests/
  grpc-requests/
  docker-compose.yml
```

## Prerequisites

- Docker Desktop
- Git Bash (recommended for `.sh` scripts on Windows)
- AWS CLI (`aws`)
- LocalStack (Pro if using ELBv2-dependent resources)

## Run with Docker Compose (Local Development)

From repository root:

```bash
docker compose up --build -d
```

Main endpoints:

- API Gateway: `http://localhost:4004`
- Auth Service: `http://localhost:4005`
- Patient Service: `http://localhost:4000`
- Billing Service: `http://localhost:4001` (gRPC: `9001`)
- Kafka: `localhost:9092` / `localhost:9094`

Stop:

```bash
docker compose down
```

## Run with LocalStack (CDK Infrastructure)

1. Build service images (example):

```bash
docker build -t auth-service:latest ./auth-service
docker build -t patient-service:latest ./patient-service
docker build -t billing-service:latest ./billing-service
docker build -t analytics-service:latest ./analytics-service
docker build -t api-gateway:latest ./api-gateway
```

2. Deploy stack from `infrastructure`:

```bash
aws --endpoint-url=http://localhost:4566 cloudformation delete-stack --stack-name patient-management
sleep 3
aws --endpoint-url=http://localhost:4566 cloudformation create-stack \
  --stack-name patient-management \
  --template-body file://./cdk.out/localstack.template.json \
  --capabilities CAPABILITY_NAMED_IAM
```

3. Check status:

```bash
aws --endpoint-url=http://localhost:4566 cloudformation describe-stacks \
  --stack-name patient-management \
  --query "Stacks[0].StackStatus" --output text
```

## Authentication Test

Default test user is seeded in `auth-service` startup:

- Email: `test@example.com`
- Password: `password123`
- Role: `PATIENT`

Direct auth-service login:

```bash
curl -X POST http://localhost:4005/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

Via API Gateway:

```bash
curl -X POST http://localhost:4004/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## API Request Collections

- Auth requests: `api-requests/auth-service/login.http`
- Patient requests: `api-requests/patient-service/*`
- Billing gRPC requests: `grpc-requests/billing-service/*`

## Notes

- On Windows, run shell scripts with Git Bash:
  - `bash ./infrastructure/localstack-deploy.sh`
- If you get JSON `400 Bad Request` on login, verify request body formatting and `Content-Type: application/json`.
- If multiple old LocalStack ECS tasks exist, remove stale `ls-ecs-*` containers before retesting.

## Credits

- Special thanks to Chris Bakely for guidance and support on this project.
