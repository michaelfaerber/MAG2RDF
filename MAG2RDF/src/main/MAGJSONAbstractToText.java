package main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MAGJSONAbstractToText {

	/** Utility method **/
	public static Map<Integer,String> toMap(JSONObject object) throws JSONException {
		Map<Integer, String> map = new HashMap<Integer, String>();

		 Iterator<String> keysItr = object.keys();
		 while(keysItr.hasNext()) {
			 String key = keysItr.next(); // e.g., "the"
			 String value = (String)object.get(key).toString(); // e.g., "[1,3,...]"
			 if(!value.startsWith("[")) {
				 map.put(Integer.valueOf(value), key);
			 }
			 else { // several numbers/indices ...
				 String[] allValues = value.split(",");
				 if(allValues != null && allValues.length > 0) {
					 allValues[0] = allValues[0].replace("[", "");
					 allValues[allValues.length-1] = allValues[allValues.length-1].replaceAll("]", "");
					 for(int i=0; i < allValues.length; i++) {
						 map.put(Integer.valueOf(allValues[i]), key);
					 }
				 }
			 }
		 }
		 return map;
		}

	/** Use this method for transformation **/
	public static String getPaperAbstractFromJSON(String JSONstring) {
//		final String jsonString = "{\"IndexLength\":207,\"InvertedIndex\":{\"In\":[0,45],\"the\":[1,4,20,28,36,40,46,49,53,59,68,83,91,95,105,112,121,127,131,140,143,151,158,164,170,174,185,192],\"Quantum\":[2,47],\"theory\":[3,48],\"wave\":[5,60],\"functions\":[6,61],\"form\":[7],\"a\":[8,63,190],\"full\":[9,64],\"and\":[10,24,26,31,65,87,157],\"orthonormalized\":[11,66],\"functional\":[12],\"space.\":[13],\"For\":[14],\"this\":[15],\"reason\":[16],\"we\":[17,187],\"can\":[18,135],\"use\":[19],\"equation\":[21,159,175],\"on\":[22],\"eigenfunctions\":[23,32],\"eigenstates\":[25],\"find\":[27],\"eigenenergies\":[29],\"$E_n$\":[30],\"$\\\\Psi_n$\":[33],\"to\":[34,101,189],\"determine\":[35],\"physical\":[37],\"characteristics\":[38],\"of\":[39,56,71,76,85,94,108,130,142,163,179,194,197],\"considered\":[41,137],\"systems\":[42],\"(or\":[43],\"models).\":[44],\"observed\":[50],\"values\":[51,70,93],\"are\":[52,99],\"average\":[54,69,92],\"value\":[55],\"operators.\":[57,77],\"Since\":[58],\"create\":[62],\"space,\":[67],\"operators\":[72,98],\"coincide\":[73],\"with\":[74,173],\"eigenvalues\":[75],\"This\":[78],\"situation\":[79],\"takes\":[80],\"place\":[81],\"in\":[82,111,118,139,169,181,184,200],\"case\":[84],\"strong\":[86],\"electromagnetic\":[88],\"interactions.\":[89,114],\"However,\":[90],\"weak\":[96,113,128,152,165],\"interaction\":[97,129],\"equal\":[100],\"zero\":[102],\"since\":[103],\"only\":[104,138],\"left-handed\":[106],\"components\":[107],\"spinors\":[109],\"participate\":[110],\"It\":[115,147],\"means\":[116],\"that\":[117,150],\"these\":[119],\"interactions\":[120,153],\"connected\":[122],\"states\":[123],\"cannot\":[124,154,205],\"exist.\":[125,206],\"Then\":[126],\"particles\":[132],\"(scatterings,\":[133],\"decays)\":[134],\"be\":[136],\"framework\":[141],\"standard\":[144],\"perturbative\":[145],\"approach.\":[146],\"is\":[148],\"shown\":[149],\"generate\":[155],\"masses\":[156],\"for\":[160,176],\"Green\\u0027s\":[161,177],\"function\":[162,178],\"interacting\":[166],\"fermions\":[167,180],\"(neutrinos)\":[168],\"matter\":[171,201],\"coincides\":[172],\"vacuum.\":[182],\"And\":[183],\"result\":[186],\"come\":[188],\"conclusion:\":[191],\"mechanism\":[193],\"resonance\":[195],\"enhancement\":[196],\"neutrino\":[198],\"oscillations\":[199],\"(i.e.\":[202],\"MSW\":[203],\"effect)\":[204]}}";
//		String jsonString = "{\"IndexLength\":115,\"InvertedIndex\":{\"A\":[0],\"method\":[1],\"of\":[2,15,58,80,90,101,103],\"resin\":[3,9,39,60,105],\"transfer\":[4],\"molding\":[5],\"with\":[6,26,37],\"a\":[7,17,20,23,66,112],\"thermoset\":[8],\"material\":[10],\"and\":[11,34,40,86],\"including\":[12],\"the\":[13,27,31,35,46,50,59,62,70,73,77,84,88,91,94,104,107],\"steps\":[14],\"forming\":[16],\"mold\":[18,28,32,51,74,108],\"defining\":[19],\"cavity,\":[21,29],\"communicating\":[22],\"hollow\":[24],\"sprue\":[25,36,47,63,71,95],\"filling\":[30],\"cavity\":[33,52,75,109],\"unpolymerized\":[38],\"catalyst\":[41],\"mixture\":[42,82,92],\"under\":[43],\"pressure,\":[44],\"refrigerating\":[45,89],\"adjacent\":[48],\"to\":[49,55,64,72,83,110],\"so\":[53],\"as\":[54],\"delay\":[56],\"polymerization\":[57,102],\"in\":[61,93,106],\"maintain\":[65],\"pressure\":[67],\"path\":[68],\"through\":[69],\"during\":[76],\"continued\":[78],\"application\":[79],\"pressurized\":[81],\"sprue,\":[85],\"maintaining\":[87],\"until\":[96],\"after\":[97],\"at\":[98],\"least\":[99],\"commencement\":[100],\"form\":[111],\"molded\":[113],\"part.\":[114]}}\r\n744209658       {\"IndexLength\":135,\"InvertedIndex\":{\"Abstract\":[0],\"We\":[3],\"studied\":[4],\"the\":[5,58,72,112],\"stochastic\":[6,113],\"nature\":[7],\"of\":[8,11,23,29],\"heterogeneous\":[9,116],\"nucleation\":[10,73,117],\"supercooled\":[12],\"liquid\":[13,63],\"water\":[14],\"by\":[15],\"molecular\":[16],\"dynamics\":[17],\"simulations.\":[18],\"The\":[19],\"systems\":[20,83],\"were\":[21,60],\"composed\":[22],\"768\":[24],\"molecules;\":[25],\"M\":[27,43,48,79,125,132],\"them\":[30],\"had\":[31],\"their\":[32],\"positions\":[33],\"restricted\":[34],\"forming\":[35],\"a\":[36,67,120],\"solid\":[37],\"nucleus\":[38],\"(\":[39,75],\"IN\":[40,87,92,97,103],\")\":[45,81],\"for\":[46,82,115],\"\u00A0=\u00A048,\":[49],\"56,\":[50],\"64,\":[51],\"72,\":[52],\"80\":[53,99],\"and\":[54,57,101,118,127],\"90\":[55,105],\"molecules,\":[56],\"rest\":[59],\"arranged\":[61],\"in\":[62],\"state.\":[64],\"By\":[65],\"using\":[66],\"statistical\":[68],\"analysis,\":[69],\"we\":[70],\"determined\":[71],\"rate\":[74],\"j\":[76,129],\"formed\":[84],\"with\":[85,111],\"64\":[89],\",\":[90,95],\"72\":[94],\".\":[106,134],\"These\":[107],\"results\":[108],\"are\":[109],\"coherent\":[110],\"hypothesis\":[114],\"show\":[119],\"direct\":[121],\"relationship\":[122],\"between\":[123]}}";
		
		Map<Integer,String> map = toMap(new JSONObject(JSONstring).getJSONObject("InvertedIndex"));
		String finalAbstractText = "";
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
//		    System.out.println(entry.getKey() + ", " + entry.getValue());
			finalAbstractText = finalAbstractText + entry.getValue() + " ";
		}
		return finalAbstractText.trim();
	}
}

