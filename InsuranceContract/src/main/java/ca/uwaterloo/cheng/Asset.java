package ca.uwaterloo.cheng;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public final class Asset {

    @Property()
    private final int Deposit;

    public int getDeposit() {
        return Deposit;
    }

    public Asset(@JsonProperty("Deposit") final int Deposit) {
        this.Deposit = Deposit;
    }
}
