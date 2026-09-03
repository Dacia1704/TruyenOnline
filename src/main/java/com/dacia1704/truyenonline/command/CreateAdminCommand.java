package com.dacia1704.truyenonline.command;

import com.dacia1704.truyenonline.command.service.AdminCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAdminCommand implements ApplicationRunner {

    private final AdminCommandService adminCommandService;

    @Override
    public void run(ApplicationArguments args) {

        if (!args.containsOption("create-admin")) {
            return;
        }

        String username = getRequiredOption(args, "username");
        String email = getRequiredOption(args, "email");
        String password = getRequiredOption(args, "password");

        adminCommandService.createAdmin(username, email, password);

        System.exit(0);
    }

    private String getRequiredOption(ApplicationArguments args, String option) {

        if (!args.containsOption(option)
                || args.getOptionValues(option) == null
                || args.getOptionValues(option).isEmpty()) {

            throw new IllegalArgumentException("Missing required option --" + option);
        }

        return args.getOptionValues(option).get(0);
    }
}
