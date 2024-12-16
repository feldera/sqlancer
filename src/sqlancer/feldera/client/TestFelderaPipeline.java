package sqlancer.feldera.client;

import org.junit.Test;

import java.util.Objects;

public class TestFelderaPipeline {
    @Test
    public void testPipelineFromJson() throws Exception {
        String json = "{\n" + "  \"id\": \"01939c8a-e0c4-7410-a3e3-e46b700540c8\",\n" + "  \"name\": \"database0\",\n"
                + "  \"description\": \"sqlancer test\",\n" + "  \"created_at\": \"2024-12-06T15:16:07.492687Z\",\n"
                + "  \"version\": 51,\n" + "  \"platform_version\": \"0.31.1\",\n" + "  \"runtime_config\": {\n"
                + "    \"workers\": 8,\n" + "    \"storage\": false,\n" + "    \"fault_tolerance\": null,\n"
                + "    \"cpu_profiler\": true,\n" + "    \"tracing\": false,\n"
                + "    \"tracing_endpoint_jaeger\": \"127.0.0.1:6831\",\n" + "    \"min_batch_size_records\": 0,\n"
                + "    \"max_buffering_delay_usecs\": 0,\n" + "    \"resources\": {\n"
                + "      \"cpu_cores_min\": null,\n" + "      \"cpu_cores_max\": null,\n"
                + "      \"memory_mb_min\": null,\n" + "      \"memory_mb_max\": null,\n"
                + "      \"storage_mb_max\": null,\n" + "      \"storage_class\": null\n" + "    },\n"
                + "    \"min_storage_bytes\": null,\n" + "    \"clock_resolution_usecs\": 100000\n" + "  },\n"
                + "  \"program_code\": \"CREATE VIEW v AS SELECT 1;\",\n" + "  \"udf_rust\": \"\",\n"
                + "  \"udf_toml\": \"\",\n" + "  \"program_config\": {\n" + "    \"profile\": null,\n"
                + "    \"cache\": true\n" + "  },\n" + "  \"program_version\": 51,\n"
                + "  \"program_status\": \"Success\",\n"
                + "  \"program_status_since\": \"2024-12-06T19:55:50.527299Z\",\n" + "  \"program_info\": {\n"
                + "    \"schema\": {\n" + "      \"inputs\": [],\n" + "      \"outputs\": [\n" + "        {\n"
                + "          \"name\": \"error_view\",\n" + "          \"case_sensitive\": false,\n"
                + "          \"fields\": [\n" + "            {\n" + "              \"name\": \"table_or_view_name\",\n"
                + "              \"case_sensitive\": false,\n" + "              \"columntype\": {\n"
                + "                \"type\": \"VARCHAR\",\n" + "                \"nullable\": false,\n"
                + "                \"precision\": -1,\n" + "                \"scale\": null,\n"
                + "                \"component\": null,\n" + "                \"fields\": null,\n"
                + "                \"key\": null,\n" + "                \"value\": null\n" + "              }\n"
                + "            },\n" + "            {\n" + "              \"name\": \"message\",\n"
                + "              \"case_sensitive\": false,\n" + "              \"columntype\": {\n"
                + "                \"type\": \"VARCHAR\",\n" + "                \"nullable\": false,\n"
                + "                \"precision\": -1,\n" + "                \"scale\": null,\n"
                + "                \"component\": null,\n" + "                \"fields\": null,\n"
                + "                \"key\": null,\n" + "                \"value\": null\n" + "              }\n"
                + "            },\n" + "            {\n" + "              \"name\": \"metadata\",\n"
                + "              \"case_sensitive\": false,\n" + "              \"columntype\": {\n"
                + "                \"type\": \"VARIANT\",\n" + "                \"nullable\": false,\n"
                + "                \"precision\": null,\n" + "                \"scale\": null,\n"
                + "                \"component\": null,\n" + "                \"fields\": null,\n"
                + "                \"key\": null,\n" + "                \"value\": null\n" + "              }\n"
                + "            }\n" + "          ],\n" + "          \"materialized\": false,\n"
                + "          \"properties\": {}\n" + "        },\n" + "        {\n" + "          \"name\": \"v\",\n"
                + "          \"case_sensitive\": false,\n" + "          \"fields\": [\n" + "            {\n"
                + "              \"name\": \"EXPR$0\",\n" + "              \"case_sensitive\": false,\n"
                + "              \"columntype\": {\n" + "                \"type\": \"INTEGER\",\n"
                + "                \"nullable\": false,\n" + "                \"precision\": null,\n"
                + "                \"scale\": null,\n" + "                \"component\": null,\n"
                + "                \"fields\": null,\n" + "                \"key\": null,\n"
                + "                \"value\": null\n" + "              }\n" + "            }\n" + "          ],\n"
                + "          \"materialized\": false,\n" + "          \"properties\": {}\n" + "        }\n"
                + "      ]\n" + "    },\n" + "    \"input_connectors\": {},\n" + "    \"output_connectors\": {}\n"
                + "  },\n" + "  \"deployment_status\": \"Shutdown\",\n"
                + "  \"deployment_status_since\": \"2024-12-06T19:55:26.508696Z\",\n"
                + "  \"deployment_desired_status\": \"Shutdown\",\n" + "  \"deployment_error\": null,\n"
                + "  \"deployment_config\": null,\n" + "  \"deployment_location\": null\n" + "}";

        FelderaPipeline p = FelderaPipeline.fromJson(json);
        assert Objects.equals(p.getName(), "database0");
        assert Objects.equals(p.getProgramCode(), "CREATE VIEW v AS SELECT 1;");
        assert Objects.equals(p.getDeploymentStatus(), "Shutdown");
        assert Objects.equals(p.getDeploymentError(), "");
        assert Objects.equals(p.getProgramStatus(), "Success");
    }
}
