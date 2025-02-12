FROM postgres:15

# Переменные окружения
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=infoWeather

# Для запуска
# docker run --name infoWeather_postgres -p 5432:5432 -d my-postgres-image