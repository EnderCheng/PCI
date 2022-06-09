package ca.uwaterloo.cheng;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;

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
