FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

# Копируем RAR-адаптер
COPY bitrix24_adapter.rar /opt/jboss/wildfly/standalone/deployments/

# Копируем настроенный standalone.xml
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

# Запуск WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
