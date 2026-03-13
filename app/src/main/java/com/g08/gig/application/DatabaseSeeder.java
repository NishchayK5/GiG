package com.g08.gig.application;

import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.models.Client;
import com.g08.gig.persistence.ClientRepository;

public final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    public static void seed(ClientUserService clientUserService,
                            ProUserService proUserService,
                            RequestService requestService,
                            ClientRepository clientRepo) {
        seedUsers(clientUserService, proUserService);
        seedRequests(requestService, clientRepo);
    }

    private static void seedUsers(ClientUserService clientUserService, ProUserService proUserService) {
        clientUserService.addClient("Client", "One", "testemailone@test.com", "TestPassOne", "TestPassOne", "9999999999", "98 Street, Winnipeg, MB");
        clientUserService.addClient("Client", "Two", "testemailtwo@test.com", "TestPassTwo", "TestPassTwo", "9999999998", "97 Street, Winnipeg, MB");
        clientUserService.addClient("Client", "Three", "testemailthree@test.com", "TestPassThree", "TestPassThree", "9999999997", "96 Street Winnipeg MB");
        proUserService.addPro("Pro", "One", "testemailfour@test.com", "9999999996", "TestPassFour", "TestPassFour", new String[]{"SkillOne", "SkillTwo"}, "Winnipeg");
        proUserService.addPro("Pro", "Two", "testemailfive@test.com", "9999999995", "TestPassFive", "TestPassFive", new String[]{"SkillOne"}, "Winnipeg");
        proUserService.addPro("Pro", "Three", "testemailsix@test.com", "9999999994", "TestPassSix", "TestPassSix", new String[]{"SkillOne", "SkillTwo", "SkillThree"}, "Edmonton");
    }

    private static void seedRequests(RequestService requestService, ClientRepository clientRepo) {
        Client placeholderClient = clientRepo.getById(1);
        if (placeholderClient == null) return;
        requestService.addRequest(placeholderClient, "Brake pad replacement",
                "My 2019 Honda Civic needs new front brake pads. There's a grinding noise when I stop.");
        requestService.addRequest(placeholderClient, "Oil change needed",
                "Due for synthetic oil change. Prefer weekend appointment if possible.");
        requestService.addRequest(placeholderClient, "AC not cooling",
                "Car AC blows warm air. 2020 Toyota Corolla. Would like a diagnosis.");
    }
}
