package lama.cockroachdb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lama.Query;
import lama.QueryAdapter;
import lama.Randomly;
import lama.cockroachdb.CockroachDBProvider.CockroachDBGlobalState;
import lama.cockroachdb.CockroachDBSchema.CockroachDBColumn;
import lama.cockroachdb.CockroachDBSchema.CockroachDBTable;

public class CockroachDBIndexGenerator {
	
	public static Query create(CockroachDBGlobalState s) {
		Set<String> errors = new HashSet<>();
		errors.add("is part of the primary index and therefore implicit in all indexes");
		errors.add("already contains column");
		errors.add("violates unique constraint");
		errors.add("schema change statement cannot follow a statement that has written in the same transaction");
		CockroachDBTable table = s.getSchema().getRandomTable(t -> !t.isView());
		StringBuilder sb = new StringBuilder("CREATE ");
		if (Randomly.getBoolean()) {
			sb.append("UNIQUE ");
		}
		sb.append("INDEX ON ");
		sb.append(table.getName());
		List<CockroachDBColumn> columns = table.getRandomNonEmptyColumnSubset();
		addColumns(sb, columns, true);
		if (Randomly.getBoolean()) {
			sb.append(" STORING ");
			addColumns(sb, table.getRandomNonEmptyColumnSubset(), false);
		}
		
		return new QueryAdapter(sb.toString(), errors);
	}

	private static void addColumns(StringBuilder sb, List<CockroachDBColumn> columns, boolean allowOrdering) {
		sb.append("(");
		for (int i = 0; i < columns.size(); i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(columns.get(i).getName());
			if (allowOrdering && Randomly.getBoolean()) {
				sb.append(" ");
				sb.append(Randomly.fromOptions("ASC", "DESC"));
			}
		}
		sb.append(")");
	}

}