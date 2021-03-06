/*
Stratagem is a model checker for transition systems described using rewriting
rules and strategies.
Copyright (C) 2013 - SMV@Geneva University.
Program written by Edmundo Lopez Bobeda <edmundo [at] lopezbobeda.net>.
This program is free software; you can redistribute it and/or modify
it under the  terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.unige.cui.smv.stratagem.modelchecker

import java.io.File
import org.slf4j.Logger
import com.typesafe.scalalogging.slf4j.Logging
import ch.qos.logback.classic.Level
import ch.unige.cui.smv.stratagem.util.IllegalTransitionSystemException
import ch.unige.cui.smv.stratagem.sigmadd.SigmaDDFactoryImpl
import ch.unige.cui.smv.stratagem.sigmadd.rewriters.SigmaDDRewriterFactory
import ch.unige.cui.smv.stratagem.util.AuxFunctions.timeAndSpace
import ch.unige.cui.smv.stratagem.util.AuxFunctions
import ch.unige.cui.smv.stratagem.sigmadd.rewriters.SigmaDDRewritingCacheStats.stats
import ch.unige.cui.smv.stratagem.util.StrategyDSL._
import ch.unige.cui.smv.stratagem.transformers.Model2TransitionSystem
import ch.unige.cui.smv.stratagem.petrinets.PetriNet
import ch.unige.cui.smv.stratagem.petrinets.Place
import ch.unige.cui.smv.stratagem.petrinets.PTModule
import ch.unige.cui.smv.stratagem.transformers.ListOfModules2TransitionSystem
import ch.unige.cui.smv.stratagem.transformers.PNML2PetriNet
import ch.unige.cui.smv.stratagem.transformers.Modularizer
import ch.unige.cui.smv.stratagem.transformers.FileModularizer
import ch.unige.cui.smv.stratagem.transformers.FileSuperModularizer
import org.eclipse.ocl.examples.xtext.oclinecore.OCLinEcoreStandaloneSetup
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.ocl.examples.xtext.oclstdlib.OCLstdlibStandaloneSetup
import org.eclipse.xtext.parser.IParser
import org.eclipse.emf.ecore.EPackage
import ch.unige.cui.smv.stratagem.xtext.TransitionSystemDslStandaloneSetup
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.validation.CheckMode
import org.eclipse.xtext.serializer.impl.Serializer
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.common.util.Diagnostic
import org.eclipse.emf.ecore.util.Diagnostician
import ch.unige.smv.cui.metamodel.adt.AdtPackage
import ch.unige.cui.smv.metamodel.ts.TransitionSystem
import ch.unige.cui.smv.metamodel.ts.SimpleStrategy
import ch.unige.cui.smv.metamodel.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.metamodel.ts.TsPackage
import scala.collection.JavaConversions._
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.common.util.TreeIterator
import ch.unige.cui.smv.stratagem.util.AlphaNumOrdering

/**
 * The main class of stratagem. It is used to launch the model checker.
 * @author mundacho
 *
 */
object Main extends Logging {

  val programName = "stratagem"
  val sversion = "0.5.0"
  val quietMode = "activate quiet mode. Only errors are printed."
  val fileComment = "the model in pnml format (using extension PNML) or native representation (extension ts)"
  val debugMode = "activate debug mode. Lots of output."
  val saturationComment = "Disable saturation."
  val stateSpaceComment = "Show state space at the end of computation"

  def main(args: Array[String]) {
    val parser = configureParser
    parser.parse(args, Config()) map { config =>
      // configure logging
      val root = org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger];
      root.setLevel(Level.DEBUG) // default log level
      if (config.quiet) root.setLevel(Level.INFO)
      if (config.verbose) root.setLevel(Level.TRACE)
      if (config.verbose && config.quiet) logger.warn("Set quiet and verbose flag at the same time")
      // register ADT and TS: WARNING must be done before creating the injector!!
      AdtPackage.eINSTANCE.eClass()
      TsPackage.eINSTANCE.eClass()
      // ocl registration
      org.eclipse.ocl.examples.pivot.OCL.initialize(null);
      org.eclipse.ocl.examples.pivot.model.OCLstdlib.install();
      org.eclipse.ocl.examples.pivot.delegate.OCLDelegateDomain.initialize(null)
      OCLinEcoreStandaloneSetup.doSetup()
      OCLstdlibStandaloneSetup.doSetup()
      val injector = (new TransitionSystemDslStandaloneSetup()).createInjectorAndDoEMFRegistration();
      if (config.mode == "transition-system") {

        logger.trace("Finished OCL registration")

        // create injector
        val resourceSet: XtextResourceSet = injector.getInstance(classOf[XtextResourceSet]);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, true);
        val uri = config.model.toURI().toString()
        val resource = resourceSet.getResource(URI.createURI(uri), true);

        val ts = resource.getContents().get(0).asInstanceOf[TransitionSystem];
        if (resource.getErrors().isEmpty()) {
          try {
            AuxFunctions.doDiagnostics(ts)
          } catch {
            case t: IllegalTransitionSystemException =>
              for (line <- t.errors) {
                logger.error(line)
              }
              System.exit(-1)
          }
          logger.trace(s"Finished loading")
          val sigmaDDFactory = SigmaDDFactoryImpl(ts.getAdt().getSignature())
          val initialState = sigmaDDFactory.create(ts.getInitialState())
          logger.debug(s"Successfully created initial state")
          val rewriter = sigmaDDFactory.rewriterFactory.transitionSystemToStateSpaceRewriter(ts)
          logger.debug(s"Successfully created state space rewriter")
          logger.debug("Starting calculation of state space")
          val stateSpace = stats(timeAndSpace(rewriter(initialState).get))
          val stateSpaceSize = stateSpace.size
          logger.debug("State space size:")
          logger.info(s"$stateSpaceSize")
          if (config.statespace) println(stateSpace.listOfTermsAsString.mkString("\n"))
        } else {
          logger.error("There were syntactic errors in your model:")
          for (error <- resource.getErrors()) {
            logger.error(s"At line ${error.getLine()}: ${error.getMessage()}")
          }
        }

      } else { // pnml file
        val model2ts: Model2TransitionSystem = createModelToTransitionSystemTransformation(config.transformation, config.clustering, config.names)
        logger.debug("Successfully processed input file")
        if (config.mode == "pn-analyzer") {
          // check config
          val petrinet = PNML2PetriNet(config.model)
          val listOfModules = Modularizer(petrinet)
          logger.debug(s"The number of modules that were detected is ${listOfModules.size}")
          logger.debug("Printing detected modules, each line represents a module:")
          logger.info(listOfModules.map(_.net.places.toList.sortBy(p => (p.id, p.name)).map { p =>
            if (config.names && config.ids) s"${p.name} (${p.id})" else if (config.ids) p.id else p.name
          }.mkString(", ")).mkString("\n"))
          if (config.transitionSystem) {
            val ts = model2ts(config.model)
            val serializer = injector.getInstance(classOf[Serializer]);
            // we need to create a resourse to be able to serialize :-S
            val resSet = new ResourceSetImpl()
            val resource = resSet.createResource(URI.createURI("temp.ts"))
            resource.getContents().add(ts)
            // we need to do the linking ourselves
            AuxFunctions.doLinking(ts)
            AuxFunctions.doDiagnostics(ts)
            logger.info(serializer.serialize(ts))
          }
        } else {
          val ts = model2ts(config.model)
          val resSet = new ResourceSetImpl()
          val resource = resSet.createResource(URI.createURI("temp.ts"))
          resource.getContents().add(ts)
          // we need to do the linking ourselves
          try {
            AuxFunctions.doLinking(ts)
            AuxFunctions.doDiagnostics(ts)
          } catch {
            case t: IllegalTransitionSystemException =>
              logger.error("There are errors in the generated transition system")
              for (error <- t.errors) {
                logger.error(error)
              }
              System.exit(-1)
          }

          if (config.transitionSystem) {
            logger.info(ts.toString)
          }
          val sigmaDDFactory = SigmaDDFactoryImpl(ts.getAdt().getSignature())
          val initialState = sigmaDDFactory.create(ts.getInitialState())
          logger.debug(s"Successfully created initial state")
          val rewriter = if (config.saturation) {
            sigmaDDFactory.rewriterFactory.transitionSystemToStateSpaceRewriterWithSaturation(ts, Identity, 2)
          } else {
            sigmaDDFactory.rewriterFactory.transitionSystemToStateSpaceRewriter(ts)
          }
          logger.debug(s"Successfully created state space rewriter")
          logger.debug("Starting calculation of state space")
          val stateSpace = stats(timeAndSpace(rewriter(initialState).get))
          val stateSpaceSize = stateSpace.size
          logger.debug("State space size:")
          logger.info(s"$stateSpaceSize")
          if (config.statespace) println(stateSpace.listOfTermsAsString.mkString("\n"))
        }

      }

    } getOrElse {
      logger.error("Unable to parse the parameters")
    }

  }

  def configureParser = new scopt.OptionParser[Config](programName) {
    head(programName + " " + sversion)
    opt[Unit]('q', "quiet") action { (_, c) =>
      c.copy(quiet = true)
    } text (quietMode)
    opt[Unit]("no-saturate") abbr ("ns") action { (_, c) =>
      c.copy(saturation = false)
    } text (saturationComment)
    opt[Unit]("state-space") abbr ("ss") action { (_, c) =>
      c.copy(statespace = true)
    } text (stateSpaceComment)
    opt[File]("clustering-file") optional () abbr ("cf") action { (x, c) =>
      c.copy(clustering = Some(x))
    } text ("option to specify the clustering file")
    opt[String]("transformation") abbr ("t") action { (x, c) =>
      c.copy(transformation = x)
    } text ("Choose the transformation for the model. Available transformations are:" +
      "\nnaive-pnml             : transforms the model naively" +
      "\nnaive-modular-pnml     : transforms the model trying to reduce its complexity by dividing it into submodules" +
      "\nanonymized-modular-pnml: transforms the model trying to reduce its complexity by dividing it into submodules. " +
      "It also will rename the places in the modules")
    opt[Unit]("debug") action { (_, c) =>
      c.copy(verbose = true)
    } text (debugMode)
    opt[Unit]("with-names") action { (_, c) =>
      c.copy(names = true)
    } text ("Print the names of the modules (from the PNML)")
    arg[File]("<file>") required () action { (x, c) =>
      c.copy(model = x)
    } text (fileComment)
    //    checkConfig { c => if ((c.clustering.isEmpty) && c.transformation != "anonymized-modular") failure("Clustering only works on anonymized-modular transformation") else success }
    cmd("transition-system") action { (_, c) =>
      c.copy(mode = "transition-system")
    } text ("transition-system mode allows to analyze a transition system")
    cmd("pn-analyzer") action { (_, c) =>
      c.copy(mode = "pn-analyzer")
    } text ("Analyzer mode allows to analyze the petri net without performing the state space calculation") children (
      opt[Unit]("modules") action { (_, c) =>
        c.copy(modules = true)
      } text ("""Print discovered modules (clusters). A module is a set of places that share some
transitions in common. The union of all modules is equal to thewhole set of
places. The intersection of two modules is empty."""),
      opt[Unit]("with-names") action { (_, c) =>
        c.copy(names = true)
      } text ("Print the names of the modules (from the PNML)"),
      opt[Unit]("with-ids") action { (_, c) =>
        c.copy(ids = true)
      } text ("Print the ids of the modules (from the PNML)"),
      opt[Unit]("print-transition-system") abbr ("ts") action { (_, c) =>
        c.copy(transitionSystem = true)
      } text ("Print the generated transition system"))
  }

  def createModelToTransitionSystemTransformation(transformationName: String, clusteringFile: Option[File], withNames: Boolean) = transformationName match {
    case "anonymized-modular" | "" =>
      clusteringFile match {
        case None =>
          new Model2TransitionSystem {
            type ModelType = PetriNet
            val file2Model = PNML2PetriNet
            type PreprocessedModelType = (List[List[List[Place]]], Set[Int], PetriNet)
            val modelPreprocessor = (model: PetriNet) => (modulesToLisOfSuperClusters(Modularizer(model)), Set[Int](), model)
            val preprocessedModel2TransitionSystem = ListOfModules2TransitionSystem.tupled

            def modulesToLisOfSuperClusters(listOfModules: List[PTModule]): List[List[List[Place]]] = {
              List(listOfModules.map(_.net.places.toList.sorted(AlphaNumOrdering)))
            }

          }
        case Some(file) =>
          new Model2TransitionSystem {
            type ModelType = PetriNet
            val file2Model = PNML2PetriNet
            type PreprocessedModelType = (List[List[List[Place]]], Set[Int], PetriNet)
            val modelPreprocessor = (model: PetriNet) => {
              val (modules, recursiveSet) = new FileSuperModularizer(file, withNames)(model)
              (modules, recursiveSet, model)
            }
            val preprocessedModel2TransitionSystem = ListOfModules2TransitionSystem.tupled
          }

      }
    case _ =>
      logger.error("Unrecognized transformation")
      scala.sys.exit(-1)
  }
}