package birt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;

public class ReportExecutor {

	private IReportEngine engine;
	private EngineConfig config;

	public ReportExecutor() throws BirtException {
		engine = null;
		config = new EngineConfig();
		config.setEngineHome("/home/aacs/birt-runtime-4_4_0/ReportEngine");
		Platform.startup(config);
		final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		engine = FACTORY.createReportEngine(config);

	}

	public void close() {
		engine.destroy();
		Platform.shutdown();
	}

	public void executeReport(InputStream rptDesign, InputStream dataSource, RenderOption renderOption) throws EngineException {
		config.getAppContext().put("org.eclipse.datatools.enablement.oda.xml.inputStream", dataSource);
		// Open the report design
		IReportRunnable design = engine.openReportDesign(rptDesign);

		IRunAndRenderTask task = engine.createRunAndRenderTask(design);
		// task.setParameterValue("Top Count", (new Integer(5)));
		// task.validateParameters();

		task.setRenderOption(renderOption);
		task.run();
		task.close();
	}

	private static RenderOption getPDFRenderOption() {
		RenderOption pdfOptions = new PDFRenderOption();
		pdfOptions.setOutputFileName("output/resample/pdftest.pdf");
		pdfOptions.setOutputFormat("pdf");
		return pdfOptions;
	}

	private static HTMLRenderOption getHTMLRenderOption() {
		HTMLRenderOption htmlOptions = new HTMLRenderOption();
		htmlOptions.setOutputFileName("output/resample/Parmdisp.html");
		htmlOptions.setOutputFormat("html");
		// HTML_OPTIONS.setHtmlRtLFlag(false);
		// HTML_OPTIONS.setEmbeddable(false);
		// HTML_OPTIONS.setImageDirectory("C:\\test\\images");
		return htmlOptions;
	}

	public static void main(String[] args) {
		ReportExecutor executor = null;
		try {
			InputStream dataSource = new ByteArrayInputStream(
					"<people><person><name>Ádá1111m</name><age>23</age></person><person><name>345345 a béka</name><age>26</age></person><person>	<name>Dorka</name><age>23</age></person></people>"
							.getBytes());
			
			InputStream rptDesign = ReportExecutor.class.getResourceAsStream("test.rptdesign");
			executor = new ReportExecutor();
			executor.executeReport(rptDesign, dataSource, getPDFRenderOption());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if (executor != null) {
				executor.close();
			}
		}
	}
}