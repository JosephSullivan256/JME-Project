package core;

import java.io.File;
import java.io.FileNotFoundException;

import com.josephsullivan256.gmail.doxml.Document;
import com.josephsullivan256.gmail.doxml.Element;
import com.josephsullivan256.gmail.doxml.TokenType;
import com.josephsullivan256.gmail.doxml.lexer.Lexer;
import com.josephsullivan256.gmail.doxml.lexer.LexerBuilder;
import com.josephsullivan256.gmail.doxml.lexer.QuoteArrayTokenMatcher;
import com.josephsullivan256.gmail.doxml.lexer.RegexTokenMatcher;
import com.josephsullivan256.gmail.doxml.lexer.TokenMatcher;
import com.josephsullivan256.gmail.doxml.parser.InvalidSyntaxException;
import com.josephsullivan256.gmail.doxml.parser.Parser;
import com.josephsullivan256.gmail.doxml.util.StringExtracter;
import com.josephsullivan256.gmail.jme.actions.IntervalSuperAction;
import com.josephsullivan256.gmail.jme.actions.SuperAction;
import com.josephsullivan256.gmail.jme.contextbuilder.GameUnitBuilder;
import com.josephsullivan256.gmail.jme.core.GameUnit;
import com.josephsullivan256.gmail.jme.util.DTParameter;
import com.josephsullivan256.gmail.jme.util.Nothing;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException{
		new Main();
	}
	
	public Main() throws FileNotFoundException{
		GameUnitBuilder gub = new GameUnitBuilder();
		populateBuilders(gub);
		
		Lexer lexer = new LexerBuilder()
				.with(TokenType.separator, new QuoteArrayTokenMatcher(new String[]{"<","<?","<!","</","?>",">","="}))
				.with(TokenType.literal, TokenMatcher.literalTokenMatcher1)
				.with(TokenType.identifier, new RegexTokenMatcher("[a-zA-Z0-9_:]*"))
				.with(TokenType.literal, TokenMatcher.literalTokenMatcher)
				.build();
		Parser parser = new Parser();
		
		String source = new StringExtracter().read(new File("res/unit.xml"));
		
		Document document = null;
		try {
			document = parser.parse(lexer.lex(source));
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		
		GameUnit unit = gub.build(document);
		unit.activate("main");
		
		while(true){
			unit.update();
		}
	}
	
	private void populateBuilders(GameUnitBuilder gub){
		populateActionBuilders(gub);
		populateDependencyBuilders(gub);
	}
	
	private void populateActionBuilders(GameUnitBuilder gub){
		
		GameUnitBuilder.SuperActionBuilder<Nothing, DTParameter> intervalSuperActionBuilder = new GameUnitBuilder.SuperActionBuilder<Nothing, DTParameter>() {
			@Override
			public SuperAction<Nothing, DTParameter> buildSuperAction(Element e) {
				String[] dtStr = e.getChild("dt").getText().split("/");
				float num = Float.parseFloat(dtStr[0]);
				float denom = Float.parseFloat(dtStr[1]);
				
				return new IntervalSuperAction(
						num/denom, 
						Float.parseFloat(e.getChild("timescale").getText()),
						true);
			}
		};
		
		populateIntervalSuperActionBuilder(intervalSuperActionBuilder);
		
		gub.addActionBuilder("interval super action", intervalSuperActionBuilder);
	}
	
	private void populateIntervalSuperActionBuilder(GameUnitBuilder.SuperActionBuilder<Nothing, DTParameter> isab){
		
		
	}
	
	private void populateDependencyBuilders(GameUnitBuilder gub){
		GameUnitBuilder.EntityCollectionBuilder ecb = new GameUnitBuilder.EntityCollectionBuilder();
		populateComponentBuilders(ecb);
	}
	
	private void populateComponentBuilders(GameUnitBuilder.EntityCollectionBuilder ecb){
		
	}
}
