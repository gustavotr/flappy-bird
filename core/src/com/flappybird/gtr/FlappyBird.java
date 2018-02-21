package com.flappybird.gtr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture passaro;
	private Texture fundo;

	//Atributos de configuração
	private int movimento = 0;
	private int larguraDispositivo;
	private int alturaDispositivo;


	@Override
	public void create () {
		batch 		= new SpriteBatch();
		passaro 	= new Texture("passaro1.png");
		fundo		= new Texture("fundo.png");

		alturaDispositivo 	= Gdx.graphics.getHeight();
		larguraDispositivo	= Gdx.graphics.getWidth();

	}

	@Override
	public void render () {

		movimento++;

		batch.begin();

		batch.draw( fundo, 0, 0, larguraDispositivo, alturaDispositivo );
		batch.draw( passaro, 30, alturaDispositivo/2 );

		batch.end();

	}
	

}
