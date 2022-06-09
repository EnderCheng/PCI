/*  Copyright (c) 2009 Omar Hasan (omar dot hasan at insa-lyon dot fr)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;

public class Paillier
{
    private final int CERTAINTY = 64;       // certainty with which primes are generated: 1-2^(-CERTAINTY)
    private int modLength;                  // length in bits of the modulus n
    private BigInteger p;                   // a random prime
    private BigInteger q;                   // a random prime (distinct from p)
    private BigInteger lambda;              // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
    private BigInteger n;                   // n = p*q
    private BigInteger nsquare;             // nsquare = n*n
    private BigInteger g;                   // 1+N
    private BigInteger mu;                  // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n

    public Paillier(int modLengthIn)
    {

        modLength = modLengthIn;
        generateKeys_fixed();
    }

    public Paillier(int modLengthIn, BigInteger p, BigInteger q, BigInteger lambda,
                    BigInteger n, BigInteger nsquare, BigInteger g, BigInteger mu)
    {
        this.modLength = modLengthIn;
        this.p = p;
        this.q = q;
        this.lambda = lambda;
        this.n = n;
        this.nsquare = nsquare;
        this.g = g;
        this.mu = mu;
    }


    private void generateKeys_fixed()
    {
        p = new BigInteger("115243904877331225657053813619749548343925507535957921245848072992991470321981343992231970185739747235974912882822238179221181324126355575877349135917133207234746890167620421243293578563467249837647367641015850125561682681567597999188687444319351274391738972214326879691847728391034064755764017723203539828599");
        q = new BigInteger("124203297932311975632421542284620597245354844196135300595969283181608447629826055720687099765475949262922840642491704932248365660226618492796812284394087399403151500426852203592879967475031105966567376113295014137521911665683955010832117176940028516723442031675843432912293478139085657809836982856995602759349");
        lambda = new BigInteger("7156836526181095711718305370459417742771924952807156849579820507137038522049454383451356370762724028008479626536483165263575070668534150095994709649332255105683780512665446355962946025783602196073430831535212728066923459090783121068887435451497453247314974129683034348250205046971796574461230697137043751610732901269652585420507011810762305146005164247114914588934459882394265838910171109858088251333742953790654030946747291989099947852373891650463246739188205638216454117246635870515965383943718629320945141180831034140639576327003155792909881363527769037167099894697224261509784465894314094991962627740693881117052");
        n = new BigInteger("14313673052362191423436610740918835485543849905614313699159641014274077044098908766902712741525448056016959253072966330527150141337068300191989419298664510211367561025330892711925892051567204392146861663070425456133846918181566242137774870902994906494629948259366068696500410093943593148922461394274087503221705249742114814042303498977428980437599608845961922399710737120963131595772149619429095572618701604080205815418808527089669442689100757369600654898687631883070806625087744365868104313925935614446105026115972932544362747001257864595840567348314917865449380793284618835623710138318747912549526256061586904822051");
        nsquare = new BigInteger("204881236249919573938751455412218416677592263559758431338104683068724650149283808136080247075071653483271197775212056384383943474755715993354305595091745277763845731062222100035567779682442861854881867381069959573243648204371006367163739704106210951322999793697589378939533269822145050316448715277030171234256418633074574939321793980404052473611323825564263788223454865694528720960911273528327580099103348315767405482751207176127738673584081037401677515403352598149916224170327618778805131080609463547375135048115232168192350698750610713370364112867650634225016161731963927578364150635950001613771582666107697012688277417719815689019004990985825672954612085075657702468555400757735786575840126235729719947474672156365333737524501882779162660174335336941127460913158308375635863063327206042111848055531082549998099897581677594491140300222811538598285425536420068394408175232051751314478568308511686623015098319875018242926865537629790638850478255522570791976147222647068001357041658049470327159627534398606822833557035201136997031368227875906899788436810601759552176340069440649872806208980267164851568726002285491228849564417022374960624558702740324223163792283734710689802848496573923391874034044283754069797011387672368715975846601");
        g = new BigInteger("14313673052362191423436610740918835485543849905614313699159641014274077044098908766902712741525448056016959253072966330527150141337068300191989419298664510211367561025330892711925892051567204392146861663070425456133846918181566242137774870902994906494629948259366068696500410093943593148922461394274087503221705249742114814042303498977428980437599608845961922399710737120963131595772149619429095572618701604080205815418808527089669442689100757369600654898687631883070806625087744365868104313925935614446105026115972932544362747001257864595840567348314917865449380793284618835623710138318747912549526256061586904822052");
        mu = new BigInteger("576890707809376986856838108052550399269081527767015458285415166362022737857412158774767188600879812521420818142355918536270465441207407894575259456723174010598814510699496497750811100151544730705141594649828413743994570330894996326709984778010401202629341007595740843053343294027729853271253188038361157982956168348627852276123863314338812605511821748039204581313758899217179066494830982335823543945562885194061704740956631008906486031692157752254925751210463748138350028527147395614291736089482432042394492390430124124732597225020651785041860576731603925247129355806411877628636345914435924518203624295789029119251");
    }

    public BigInteger getP()
    {
        return p;
    }

    public BigInteger getQ()
    {
        return q;
    }

    public BigInteger getLambda()
    {
        return lambda;
    }

    public int getModLength()
    {
        return modLength;
    }

    public BigInteger getN()
    {
        return n;
    }

    public BigInteger getNsquare()
    {
        return nsquare;
    }

    public BigInteger getG()
    {
        return g;
    }

    public BigInteger getMu()
    {
        return mu;
    }

    public void generateKeys()
    {
        p = new BigInteger(modLength / 2, CERTAINTY, new SecureRandom());     // a random prime

        do
        {
            q = new BigInteger(modLength / 2, CERTAINTY, new SecureRandom()); // a random prime (distinct from p)
        }
        while (q.compareTo(p) == 0);

        // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
        lambda = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))).divide(
                p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        n = p.multiply(q);              // n = p*q
        nsquare = n.multiply(n);        // nsquare = n*n

        g = n.add(BigInteger.ONE);

        // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n
        mu = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
    }

    public BigInteger encrypt(BigInteger m) throws Exception
    {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0)
        {
            throw new Exception("Paillier.encrypt(BigInteger m): plaintext m is not in Z_n");
        }

        // generate r, a random integer in Z*_n
        BigInteger r = Utils.randomZStarN(modLength,n);

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger add(BigInteger c1, BigInteger c2)
    {
        BigInteger c = c1.multiply(c2).mod(nsquare);
        return c;
    }

    public BigInteger multiply(BigInteger c, BigInteger m)
    {
        BigInteger c_m = c.modPow(m,nsquare);
        return c_m;
    }

    public BigInteger encrypt(BigInteger m, BigInteger r) throws Exception
    {
        // if m is not in Z_n
        if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0)
        {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): plaintext m is not in Z_n");
        }

        // if r is not in Z*_n
        if (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1)
        {
            throw new Exception("Paillier.encrypt(BigInteger m, BigInteger r): random integer r is not in Z*_n");
        }

        // c = g^m * r^n mod n^2
        return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
    }

    public BigInteger decrypt(BigInteger c) throws Exception
    {
        // if c is not in Z*_{n^2}
        if (c.compareTo(BigInteger.ZERO) < 0 || c.compareTo(nsquare) >= 0 || c.gcd(nsquare).intValue() != 1)
        {
            throw new Exception("Paillier.decrypt(BigInteger c): ciphertext c is not in Z*_{n^2}");
        }

        // m = L(c^lambda mod n^2) * mu mod n, where L(u) = (u-1)/n
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(mu).mod(n);
    }

    public void printValues()
    {
        System.out.println("p:       " + p);
        System.out.println("q:       " + q);
        System.out.println("lambda:  " + lambda);
        System.out.println("n:       " + n);
        System.out.println("nsquare: " + nsquare);
        System.out.println("g:       " + g);
        System.out.println("mu:      " + mu);
    }

    // return public key as the vector <n, g>
    public Vector publicKey()
    {
        Vector pubKey = new Vector();
        pubKey.add(n);
        pubKey.add(g);

        return pubKey;
    }
}
