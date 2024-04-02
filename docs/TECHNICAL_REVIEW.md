# Technical Review

## Table of Contents

- [Introduction](#introduction)
- [Overall Architecture](#overall-architecture)
- [View](#view)
- [ViewModel](#viewmodel)
- [Model](#model)
- [Network Payloads](#network-payloads)

## Introduction

This document is a technical review of the project `PPM (Proximity Pushups Monsters) for Android`. It aims to explain a detailled summury of the implementation choices made during the development of the project. The project is part of the course `INFO4` at `Polytech Grenoble` in the `2023-2024` school year.

## Overall Architecture
We worked with an MVVM architecture. We will explain the different parts of the architecture in the following sections, but for now, here is a simple diagram that represents the overall archtiecture.

![Architecture](assets/overall_diagram.png)

- View : The view regroup the XML files that represent the visual part of the application and the activities that manage the interactions with the user.

- ViewModel : The ViewModel manage the communication between the View and the Model, and finally the payloads treatement (sending & treating). Most of the logic of the application is in this part.

- Model : The Model is here to deal with the game logic and data. It is the part that contains the game main rules and the data that the game needs to work.

- ConnectionService (from NearbyConnection): The ConnectionService is an object that represents the network interface of the application. We will not detail this part in this document because it's not made by us, but you can find more information about it in the [Google Nearby Connections API documentation](https://developers.google.com/nearby/connections/overview).

## View
TODO

## ViewModel
For the sake of this project, our ViewModel (`ViewModelPMU`) is an abstract class that has two subclasses :
- `ViewModelHost`
- `ViewModelClient`

These two classes have the same goal, while being implemented each in their own specific way. 

### ViewModelHost


## Model
TODO

## Network Payloads 
The payload system already exists in the Nearby Connections API. 
The only remaining questions are how to manage the payloads in the ViewModel.
### Representation
Data will be represented using a json consistant format.
This offers a simple way to represent data and to parse it while keeping a human-readable format.
It also allows to easily add new actions and parameters to the protocol.
The json format will be as follows:
```json
{
    "action": "<ACTION>",
    "sender": "<HOST | CLIENT>"
    "params": {
        "<PARAM1>": "<VALUE1>",
        "<PARAM2>": "<VALUE2>",
        ...
    }
}
```

*Exemple* for a `VOTE` action:
```json
{
    "action": "VOTE",
    "sender": "HOST",
    "params": {
        "puuid": "A39BH49G",
        "vote": true
    }
}
```
*There is no need to send the `sender` field as it is implicit in the connection, it is only used for a debug purpose*

Lastly, some more complex objets needs to be transfered for some specific actions (`GAME_START`, `PLAYER_LIST`, etc).
For these cases, each object that needs to be transfered will have to implement the `Jsonizable` interface.
This interface will have two methods:
- `toJson()` : that will return the json representation of the object
- `fromJson(JSONObject json)` : that will return the object from the json representation


### Sending
There is not much to say about sending the payloads, as it is just using the `sendPayload` method from the Nearby Connections API.

However, We've made an easy way for building the payloads in the ViewModel.

The `PayloadMaker` class. 

### Treatment

