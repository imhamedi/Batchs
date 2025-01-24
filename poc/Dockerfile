# Étape 1 : Utiliser une image Java officielle
FROM openjdk:17-jdk-slim

# Étape 2 : Définir le répertoire de travail
WORKDIR /app

# Étape 3 : Copier le fichier JAR généré par votre application
COPY target/poc-0.0.1-SNAPSHOT.jar app.jar

# Étape 4 : Copier les fichiers nécessaires
COPY src/main/resources/db/Product_Queries.sql /app/db/Product_Queries.sql

# Étape 5 : Ajouter une commande pour exécuter l'application
CMD ["java", "-jar", "app.jar"]
