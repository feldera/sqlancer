package sqlancer.feldera.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

public class FelderaPipeline {
    private final String name;
    private final String programCode;
    private final String deploymentStatus;
    private final String programStatus;
    private final String deploymentError;
    private final int version;

    public FelderaPipeline(HashMap<String, Object> resp) throws FelderaException {
        this.name = resp.get("name").toString();
        this.programCode = resp.get("program_code").toString();
        this.deploymentStatus = resp.get("deployment_status").toString();
        this.deploymentError = Optional.ofNullable(resp.get("deployment_error")).map(Object::toString).orElse("");
        this.version = Integer.parseInt(resp.get("version").toString());

        if (resp.get("program_status") instanceof String) {
            this.programStatus = resp.get("program_status").toString();
        } else {
            throw new FelderaException(String.format("err: pipeline %s failed to compile:\n%s", name,
                    resp.get("program_status").toString()));
        }
    }

    public static FelderaPipeline fromJson(String json) throws JsonProcessingException, FelderaException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Object.class);

        return new FelderaPipeline(objectMapper.readValue(json, mapType));
    }

    public String getName() {
        return name;
    }

    public String getProgramCode() {
        return programCode;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public String getProgramStatus() {
        return programStatus;
    }

    @Nullable
    public String getDeploymentError() {
        return deploymentError;
    }

    public int getVersion() {
        return this.version;
    }

}
