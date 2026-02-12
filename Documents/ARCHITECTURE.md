# ARCHITECTURE

## Packages and Components

Our source code is in ``go8-project\app\src\main\java\com\g08\gig`` and is split into 5 packages:
- Application - Main project files that run on startup
- Business - Validation model and logic services
- Models - Objects (such as User, Pro and Client)
- Persistence - SQLite database/interface and fake stub database/interface
- Presentation - UI and Android Activities

## Responsibilities Between Layers

### UI Layer

The UI layer currently handles presenting the login page, the pro user creation page, the client user creation page, and the home landing pages for both pro and client users. It shows each of these pages to the users and allows them to enter text into fields which is then given to the logic layer.

### Logic Layer

The logic layer acts as a middle man between the UI and data layer. This layer validates inputted data from the UI to make sure the user has given expected input, then creates the required objects and writes them to the data layer. This layer is also used to read from the data layer for operations such as logging in or checking for unique email logins.

### Data Layer 

The persistence layer has repository interfaces which are implemented by either our real repositories (which use SQLite), or our fake stub repositories (which use an ArrayList to mock a database). This layer is used to store data that is given to it by the logic layer. This data can then be read or written to by the logic layer.

## High Level Interaction Overview

- The presentation component interacts directly with the business component
    - Activities (such as Login, CreateClientUser, CreateProUser) collect user input and call the correct service (AuthService, ClientUserService, ProUserService)
- The business component interacts with repository interfaces in the persistence component
    - Objects are created in the business component then calls the persistence layer to store or retrieve data
    - Abstraction in the persistence layer allows the data storage system to change without affecting the other components
- The data is returned back up the components the same way it moved down
    - The persistence component returns data to the business layer
    - The business component processes the result
    - The presentation layer updates the UI with the new data


## Architecture Diagram

![Architecture Diagram](../../../Downloads/Architecture_Model_GiG.jpg)



