package sqlancer.feldera;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import sqlancer.DBMSSpecificOptions;

@Parameters(separators = "=", commandDescription = "Feldera (default "
        +  FelderaOptions.DEFAULT_URL + ")")
public class FelderaOptions implements DBMSSpecificOptions<FelderaOracleFactory> {

    public static final String DEFAULT_URL = "http://localhost:8080";

    @Parameter(names = "--oracle", description = "Specifies which test oracle should be used for Feldera")
    public List<FelderaOracleFactory> oracle = List.of(FelderaOracleFactory.NOREC);

    @Parameter(names = "--connection-url", description = "Specifies the URL for connecting to the Feldera", arity = 1)
    public String connection_url = DEFAULT_URL;

    @Override
    public List<FelderaOracleFactory> getTestOracleFactory() {
        return oracle;
    }

}
