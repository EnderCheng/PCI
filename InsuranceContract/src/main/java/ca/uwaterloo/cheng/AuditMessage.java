package ca.uwaterloo.cheng;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class AuditMessage {

    @Property()
    private int num;

    public int getNum() {
        return num;
    }

    public AuditMessage(@JsonProperty("num") int num)
    {
        this.num = num;
    }
}
