package test.lombox;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(of = "dateIntervention")
@ToString
@Getter
public class Intervention implements Serializable, Comparable<Intervention> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	LocalDate				  dateIntervention;
	Long					  kilometrage;
	@Setter
	String					  libelle;
	@Setter
	BigDecimal				  prix;

	@Override
	public int compareTo(Intervention o) {
		return this.dateIntervention.compareTo(o.getDateIntervention());
	}
}
