package test.lombox;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "numeroMoteur", "numeroChassis" })
@ToString(of = { "numeroMoteur", "numeroChassis", "numeroImmatriculation", "dateMiseEnCirculation" })
public class Vehicule implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	LocalDate				  dateMiseEnCirculation;
	// champs de relation
	List<Intervention> interventions = new ArrayList<>();
	String			   numeroChassis;
	String			   numeroImmatriculation;
	// champs métier
	String numeroMoteur;
}
