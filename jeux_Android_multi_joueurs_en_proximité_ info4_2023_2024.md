# <a name="_qh81iice25it"></a>29 Janvier
## <a name="_nrxk2ecqg00q"></a>Tâches réalisées:
- Trouver quel jeu modéliser. 
- Veille technologique : recherches pour savoir comment implémenter de la connexion de proximité. Étude du code de frozen bubble (f droid).
- Création et configuration d’un espace de travail, de gestion de projet (Jira, Confluence)
## <a name="_2w7iettzf4vg"></a>Résultats:
- Découverte de l’API de Google : Nearby Connections. Recherche de documentation sur cette API qui semble convenir à nos besoins.
- Décision d’utiliser Android Studio et le langage Kotlin pour implémenter l’application et xml pour le visuel.

# <a name="_llgi5gccyokv"></a>12 Février
## <a name="_co505vx3sn03"></a>Tâches réalisées:
- Apprentissage de l’IDE Android Studio et du langage Kotlin.
- Développement du prototype de connexion entre 3 appareils à l’aide de l’API Nearby Connection.
- Création de documents de gestion de projet (Matrice SWOT, Cahier des charges, Gantt, personna…)
## <a name="_i68q8lwpk3hn"></a>Résultats:
- Découverte des difficultés de la simulation d’une connexion à 3 personnes, en effet l’émulateur intégré à l’IDE crash constamment sur un ordinateur pas assez puissant. Nous devons donc interconnecter nos téléphones. On doit donc être plusieurs pour pouvoir tester l’application.
- Une meilleure vision du projet vis-à-vis du périmètre du  jeu et de l’implémentation.
# <a name="_xy5llrhdefkf"></a>4 Mars
## <a name="_1fvqivcmdns2"></a>Tâches réalisées:
- Diagramme de classe
- Diagramme de séquence de deux tours d’une partie de jeu.
- Création des visuels de l’ensemble des pages de notre jeu, ainsi que le lien entre les pages sur l’application figma.
## <a name="_muzq53lau5t"></a>Résultats:
- 1ère version du diagramme de classe UML. Maquettes des différentes pages du jeu dessinées avec Figma.
# <a name="_lu0mkp1xt7k7"></a>11 Mars
## <a name="_6t5hd1srmzg5"></a>Tâches réalisées:
- Initialisation du projet et de son squelette architectural.
- Corriger et améliorer le diagramme de classe, discussion sur l’architecture du jeu.
- Codage du visuel des premières pages de l’application (pseudo\_choice, home\_page), aucune interaction pour le moment.
## <a name="_im0f1bjzxc7p"></a>Résultats:
- Élaboration d’un diagramme de séquence pour 2 tours de jeu.
- Les visuels sont fonctionnels sur l’émulateur d’android studio et sur un téléphone physique.
# <a name="_c728uo6tpwe"></a>12 Mars
## <a name="_f3nd50grw8ve"></a>Tâches réalisées:
- Création des pages importantes en xml
- Création globale des ViewModel pour l’host et le client et établissement d’une connexion entre les deux à partir du code source d’un jeu de morpion trouvé sur f droid.
## <a name="_j3qu6rhp8hjx"></a>Résultats:
- Système de traitement de payloads robuste à l’aide d’un JSON.
- Le visuel de l’ensemble des pages de l’application est fonctionnel.

# <a name="_95xd2ywdota8"></a>14 Mars
## <a name="_wt68jmy2kfbu"></a>Tâches réalisées:
- Répertorier les différentes interactions entre host et client (Payload). En déduire le format des requêtes (données,etc…) et leur traitement.
- Implémentation de premiers liens entre des pages. 
## <a name="_20pajkleo9vp"></a>Résultats:
- Les 4 premières pages de l’application sont liées. On peut désormais naviguer entre les premières pages de notre application. On peut désormais atteindre les pages de connexion et mettre en place celle-ci.
