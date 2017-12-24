Ce deployeur va lire les paramètres de contexte de context.properties pour déploier tous les services, routes
vers un serveur TAC, qui correspondent à la version passée en paramètre.
! actuellement les jobs doivent être configurés à la main.
! il faut vérifier que les services et routes de version précédentes sont désinstallées (ou le faire: sensible à la casse)
! il faut ensuite déploier et starter les services et routes depuis le TAC.
Note: les bundles et features Karaf possédant le même nom que les versions déployées sont préalablement désinstallées et supprimées,
      afin que le conteneur Karaf ne possède que les versions souhaitées (!sensible à la casse)
