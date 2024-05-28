package me.jameschan.burrow.client;

import me.jameschan.burrow.kernel.common.BurrowRequest;
import me.jameschan.burrow.kernel.utility.CommandUtility;

public class Burrow {
  public static void main(final String[] args) throws BurrowClientInitializationException {
    final var command = CommandUtility.getOriginalCommand(args);
    final var client = new HttpBurrowClient();
    final var request = new BurrowRequest();
    request.setCommand(command);

    final var response = client.sendRequest(request);
    System.out.println(response.getMessage());
    System.exit(response.getCode());
  }
}
