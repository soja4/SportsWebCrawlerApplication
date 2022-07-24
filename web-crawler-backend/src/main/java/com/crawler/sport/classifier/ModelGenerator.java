package com.crawler.sport.classifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.SerializationHelper;

@Slf4j
@Component
public class ModelGenerator {

    public Classifier buildClassifier(Instances traindataset) {
        MultilayerPerceptron m = new MultilayerPerceptron();

        // m.setGUI(true);
        // m.setValidationSetSize(0);
        // m.setBatchSize("100");
        // m.setLearningRate(0.3);
        // m.setSeed(0);
        // m.setMomentum(0.2);
        // m.setTrainingTime(500);//epochs
        // m.setNormalizeAttributes(true);

        /*Multipreceptron parameters and its default values
               *Learning Rate for the backpropagation algorithm (Value should be between 0 - 1, Default = 0.3).
               *m.setLearningRate(0);

        *Momentum Rate for the backpropagation algorithm (Value should be between 0 - 1, Default = 0.2).
        *m.setMomentum(0);

               *Number of epochs to train through (Default = 500).
               *m.setTrainingTime(0)

        *Percentage size of validation set to use to terminate training (if this is non zero it can pre-empt num of epochs.
         (Value should be between 0 - 100, Default = 0).
               *m.setValidationSetSize(0);

        *The value used to seed the random number generator (Value should be >= 0 and and a long, Default = 0).
               *m.setSeed(0);

               *The hidden layers to be created for the network(Value should be a list of comma separated Natural
        numbers or the letters 'a' = (attribs + classes) / 2,
        'i' = attribs, 'o' = classes, 't' = attribs .+ classes) for wildcard values, Default = a).
                *m.setHiddenLayers("2,3,3"); three hidden layer with 2 nodes in first layer and 3 nodends in second and 3 nodes in the third.

               *The desired batch size for batch prediction  (default 100).
               *m.setBatchSize("1");
                */
        try {
            m.buildClassifier(traindataset);

        } catch (Exception ex) {
            log.error("Error building classifier");
        }
        return m;
    }

    public String evaluateModel(Classifier model, Instances traindataset, Instances testdataset) {
        Evaluation eval = null;
        try {
            // Evaluate classifier with test dataset
            eval = new Evaluation(traindataset);
            eval.evaluateModel(model, testdataset);
        } catch (Exception ex) {
            log.error("Error evaluating model: {}", ex.getLocalizedMessage());
        }
        return eval.toSummaryString("", true);
    }

    public void saveModel(Classifier model, String modelPath) {

        try {
            SerializationHelper.write(modelPath, model);
        } catch (Exception ex) {
            log.error("Error saving model");
        }
    }
}
