package bootcamp;

import net.corda.core.contracts.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        List<ContractState> inputs = tx.getInputStates();
        List<ContractState> outputs = tx.getOutputStates();
        List<CommandWithParties<CommandData>> commands = tx.getCommands();

        if (!(inputs.isEmpty())) {
            throw new IllegalArgumentException("No inputs allowed");
        }

        if (!(outputs.size() == 1)) {
            throw new IllegalArgumentException("Only 1 output allowed");
        }

        if (!(commands.size() == 1)) {
            throw new IllegalArgumentException("Only 1 command allowed");
        }

        ContractState output = outputs.get(0);

        if (!(output instanceof TokenState)) {
            throw new IllegalArgumentException("Output must be a token state");
        }

        if (!(((TokenState) output).getAmount() > 0)) {
            throw new IllegalArgumentException("Output amount must be positive");
        }

        CommandWithParties<CommandData> command = commands.get(0);

        if (!(command.getValue() instanceof Commands.Issue)) {
            throw new IllegalArgumentException("Command must be a issue command");
        }

        Party issuer = ((TokenState) output).getIssuer();
        List<PublicKey> requiredSigners = command.getSigners();

        if (!(requiredSigners.contains(issuer.getOwningKey()))) {
            throw new IllegalArgumentException("Issuer must be a required signer");
        }
    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}
