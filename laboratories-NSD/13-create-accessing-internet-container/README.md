# Create an accessing Internet container

### Step 1
Creare e lanciare il nuovo container in GNS3 VM (scripts/gns3vm/CreateAndExecContainer.sh):

    docker run -it -d weibeld/ubuntu-networking
    docker exec -it $CONTAINER_ID bash

### Step 2
Eseguire "apt update" nel nuovo container (scripts/container/Update.sh):

    apt update
    exit

### Step 3
Creare in GNS3 VM un'immagine associata al nuovo container (scripts/gns3vm/CommitImage.sh):

    docker commit $CONTAINER_ID my-gns3-container

### Step 4
Importare il nuovo container in GNS3 creando manualmente un nuovo template e selezionando un'immagine esistente (my-gns3-container:latest).