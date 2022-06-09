
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.math.BigInteger;

@DataType()
public class NoAuditMessage {

    private String choice;

    public String getChoice() {
        return choice;
    }

    public NoAuditMessage(@JsonProperty("choice") String choice)
    {
        this.choice  = choice;
    }
}
