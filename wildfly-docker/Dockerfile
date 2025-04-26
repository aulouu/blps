FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17

# Копируем WAR-адаптер
COPY bitrix24_adapter.war /opt/jboss/wildfly/standalone/deployments/

# Копируем настроенный standalone.xml
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

RUN $JBOSS_HOME/bin/add-user.sh -u admin -p admin --silent

# Запуск WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
