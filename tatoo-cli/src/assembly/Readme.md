tatoo-cli Documentation
=======================


tatoo-cli ?
-----------
tatoo-cli est un outil de contrôle d'environnement Talend permettant de faciliter les opérations d'exploitation.
Il se compose d'un JAR autosuffisant se basant sur un fichier de propriétés.

tatoo-cli permet de:
* Cleaner un TAC et un Karaf
* (Dés-)Installer des jobs, services et routes dans le TAC (sans les déploier) depuis Nexus
* (Dé-)Déploier des jobs, services et routes vers Karaf ou le JobServer
* Starter et Stopper des services et routes
* Afficher le status des composants applicatifs (nexus, tac, rjs, webservices + contrôle wadl/wsdl)
* Créer des Trigger TAC



Utilisation
-----------

tatoo-cli propose des commandes principales ou spécialisées. Exemple:
* deploy
* deploy:routes
* deploy:services
* deploy:jobs
Le premier (deploy) inclut les trois spécialisations (routes, services, jobs)

Pour le détail des commandes, faire un appel avec --help.


tatoo-cli se veut simple d'utilisation. Les actions principales comprennent une expression regexp permettant de filtrer la portée de l'action.
Pour les opérations d'installation, le mode --drymode permet de simuler l'action: les deploy-undeploy ne vont pas être réalisées, mais la commande est affichée à l'écran.
Les logs de sortie peuvent être lues sur la console (standard) ou json (--json), ou dans le cas de la commande Status, la commande (--onlyStatus) devrait n'afficher que le status et son message associé.
Les commandes sont exécutées à partir d'un fichier de propriétés, par défaut tatoo-cli.properties, ou spécifique si (--properties=...)

Les paramètres --linuxUsername et --linuxPassword ne sont actuellement pas utilisés (essai en cours pour stopper-starter complètement un environnement Tatoo via une seule commande).

Si il manque des commandes, merci de le signaler à l'équipe de dev.


Technique
---------
tatoo-cli se repose sur Talend Metaservlet (exposé par le TAC):
https://help.talend.com/reader/XKiHN_uvNCRjt1~QcNn4fw/URmRoWm7YAuFC06smWxO9g
Certaines commandes effectuent plusieurs appels Metaservlet, et peuvent ajouter de la logique applicative.


Futur
-----
Actuellement, la création de trigger pour les executionPlan est documenté mais ne semble pas fonctionner (bug Talend ?)
A venir: création des users automatisé ?
A venir: autres actions Metaservlet ?
A venir: déploiement du NET automatisé par CommandLine ?

