package ca.uwaterloo.cheng;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;

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
