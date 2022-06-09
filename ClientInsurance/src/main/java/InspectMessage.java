import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.math.BigInteger;

@DataType()
public class InspectMessage {

    @Property()
    private boolean res;

    public boolean getRes() {
        return res;
    }

    public InspectMessage(@JsonProperty("res") boolean res)
    {
        this.res = res;
    }
}
