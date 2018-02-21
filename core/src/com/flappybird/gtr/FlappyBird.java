package com.flappybird.gtr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random randomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	//private ShapeRenderer shape;

	//Atributos de configuração

	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoDoJogo = 0;	// 0 -> Não iniciado; 1 -> Iniciado; 2 -> Game Over
	private int pontuacao = 0;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical = 0;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private int alturaEntreCanosRandomica;
	private boolean marcouPonto = false;

	//Câmera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;


	@Override
	public void create () {
		batch 		= new SpriteBatch();
		//shape		= new ShapeRenderer();
		passaros 	= new Texture[3];
		passaros[0]	= new Texture("passaro1.png");
		passaros[1]	= new Texture("passaro2.png");
		passaros[2]	= new Texture("passaro3.png");
		fundo		= new Texture("fundo.png");
		canoBaixo	= new Texture("cano_baixo.png");
		canoTopo	= new Texture("cano_topo.png");
		gameOver	= new Texture("game_over.png");
		passaroCirculo		= new Circle();
		retanguloCanoBaixo 	= new Rectangle();
		retanguloCanoTopo	= new Rectangle();

		fonte		= new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem 	= new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		/****
		 * Configuração da camera
		 */
		camera		= new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport 	= new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		alturaDispositivo 	= VIRTUAL_HEIGHT;
		larguraDispositivo	= VIRTUAL_WIDTH;
		posicaoInicialVertical 			= alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal 	= larguraDispositivo;
		espacoEntreCanos = 300;
		randomico = new Random();

	}

	@Override
	public void render () {

		camera.update();

		// Limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if (variacao > 2) variacao = 0;

		if( estadoDoJogo == 0 ){	//Não iniciado
			if( Gdx.input.justTouched() ){
				estadoDoJogo = 1;
			}
		}else {//Iniciado

			velocidadeQueda++;
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
				posicaoInicialVertical -= velocidadeQueda;

			if( estadoDoJogo == 1){
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				if (Gdx.input.justTouched()) {
					//Gdx.app.log("Toque", "Toque na tela");
					velocidadeQueda = -15;
				}

				// verifica se o cano saiu da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = randomico.nextInt(400) - 200;
					marcouPonto = false;
				}

				// verifica pontuacao
				if(posicaoMovimentoCanoHorizontal < larguraDispositivo/3){
					if( !marcouPonto ) {
						marcouPonto = true;
						pontuacao++;
					}
				}

			}else{ //Game over
				if ( Gdx.input.justTouched() ){
					estadoDoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo/2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}
			}
		}

		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw( fundo, 0, 0, larguraDispositivo, alturaDispositivo );
		batch.draw( canoTopo,  posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw( canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

		batch.draw( passaros[ (int) variacao], larguraDispositivo / 3 , posicaoInicialVertical );
		fonte.draw( batch, String.valueOf( pontuacao ), larguraDispositivo/2, alturaDispositivo - 50);

		if(estadoDoJogo == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo / 2);
			mensagem.draw(batch, "Toque para Reiniciar!", larguraDispositivo/2 - 220, alturaDispositivo/2 - gameOver.getHeight() / 2);
		}

		batch.end();


		passaroCirculo.set( larguraDispositivo / 3 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
		retanguloCanoBaixo.set(posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(),
				canoBaixo.getHeight()
		);
		retanguloCanoTopo.set(posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(),
				canoTopo.getHeight()
		);

		// Desenhar formas
		//shape.begin(ShapeRenderer.ShapeType.Filled);
		//shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
		//shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
		//shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
		//shape.setColor(Color.RED);
		//shape.end();


		// teste de colisao
		if( Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
			|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ){
				//Gdx.app.log("Colisão", "Houve colisão");
				estadoDoJogo = 2;
		}

	}

	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
	}

}
