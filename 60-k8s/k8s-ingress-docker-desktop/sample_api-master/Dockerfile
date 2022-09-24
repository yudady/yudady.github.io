FROM mcr.microsoft.com/dotnet/core/sdk:3.1.101 AS build-env
WORKDIR /app

COPY ./src/routing.csproj ./
RUN dotnet restore

COPY ./src/ ./
RUN dotnet publish -c Release -o out

FROM mcr.microsoft.com/dotnet/core/aspnet:3.1-alpine
WORKDIR /app
COPY --from=build-env /app/out .
ENTRYPOINT ["dotnet", "routing.dll"]