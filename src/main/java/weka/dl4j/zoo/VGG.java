/*
 * WekaDeeplearning4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WekaDeeplearning4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WekaDeeplearning4j.  If not, see <https://www.gnu.org/licenses/>.
 *
 * VGG16.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, New Zealand
 */

package weka.dl4j.zoo;

import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.ZooModel;
import weka.dl4j.Preferences;
import weka.dl4j.PretrainedType;

/**
 * A WEKA version of DeepLearning4j's VGG16 ZooModel.
 *
 * @author Steven Lang
 * @author Rhys Compton
 */
public class VGG extends AbstractZooModel {

    // Pretrained weights notes:
    // VGG16 CIFAR10 -  Download link possibly broken on DL4J end?
    //            The downloaded zip for these weights is only 10mb vs 513mb for Imagenet

    private static final long serialVersionUID = -6728816609851L;

    public enum VARIATION {VGG16, VGG19}

    ;

    protected VARIATION m_variation = VARIATION.VGG16;

    public VGG() {
        setPretrainedType(PretrainedType.IMAGENET);
    }

    public void setVariation(VARIATION var) {
        m_variation = var;

        setPretrainedType(m_pretrainedType);
    }

    @Override
    public void setPretrainedType(PretrainedType pretrainedType) {
        if (m_variation == VARIATION.VGG16) {
            if (pretrainedType == PretrainedType.VGGFACE) {
                // VGGFace pretrained has slightly different network structure to Imagenet pretrained
                setPretrainedType(pretrainedType, 4096, "fc7", "fc8");
            } else {
                setPretrainedType(pretrainedType, 4096, "fc2", "predictions");
            }
        } else if (m_variation == VARIATION.VGG19) {
            setPretrainedType(pretrainedType, 4096, "fc2", "predictions");
        }
    }

    public ComputationGraph init(int numLabels, long seed, int[] shape, boolean filterMode) {
        ZooModel net = null;
        if (m_variation == VARIATION.VGG16) {
            net = org.deeplearning4j.zoo.model.VGG16.builder()
                    .cacheMode(CacheMode.NONE)
                    .workspaceMode(Preferences.WORKSPACE_MODE)
                    .inputShape(shape)
                    .numClasses(numLabels)
                    .build();
        } else if (m_variation == VARIATION.VGG19) {
            net = org.deeplearning4j.zoo.model.VGG19.builder()
                    .cacheMode(CacheMode.NONE)
                    .workspaceMode(Preferences.WORKSPACE_MODE)
                    .inputShape(shape)
                    .numClasses(numLabels)
                    .build();
        }

        ComputationGraph defaultNet = net.init();

        return attemptToLoadWeights(net, defaultNet, seed, numLabels, filterMode);
    }

    @Override
    public int[][] getShape() {
        if (m_variation == VARIATION.VGG16)
            return org.deeplearning4j.zoo.model.VGG16.builder().build().metaData().getInputShape();
        else
            return org.deeplearning4j.zoo.model.VGG19.builder().build().metaData().getInputShape();
    }
}