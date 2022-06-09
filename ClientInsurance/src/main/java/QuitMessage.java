import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.math.BigInteger;

@DataType()
public class QuitMessage {

    private String decision;

    public String getDecision() {
        return decision;
    }

    public QuitMessage(@JsonProperty("decision") String decision)
    {
        this.decision = decision;
    }
}
