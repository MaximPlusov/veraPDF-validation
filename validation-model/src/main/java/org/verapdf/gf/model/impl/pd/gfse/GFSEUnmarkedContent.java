/**
 * This file is part of veraPDF Validation, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Validation is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Validation as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Validation as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gf.model.impl.pd.gfse;

import org.verapdf.as.ASAtom;
import org.verapdf.gf.model.impl.operator.inlineimage.GFOp_EI;
import org.verapdf.gf.model.impl.operator.pathpaint.GFOpPathPaint;
import org.verapdf.gf.model.impl.operator.pathpaint.GFOp_n;
import org.verapdf.gf.model.impl.operator.shading.GFOp_sh;
import org.verapdf.gf.model.impl.operator.textshow.GFOpTextShow;
import org.verapdf.gf.model.impl.operator.textshow.GFOp_TJ_Big;
import org.verapdf.gf.model.impl.operator.textshow.GFOp_Tj;
import org.verapdf.gf.model.impl.operator.xobject.GFOp_Do;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.operator.Operator;
import org.verapdf.model.pdlayer.PDXObject;
import org.verapdf.model.selayer.SEContentItem;
import org.verapdf.model.selayer.SEUnmarkedContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxim Plushchov
 */
public class GFSEUnmarkedContent extends GFSEContentItem implements SEUnmarkedContent {

    public static final String UNMARKED_CONTENT_TYPE = "SEUnmarkedContent";

    private String defaultLang;

    public GFSEUnmarkedContent(List<Operator> operators, String parentStructureTag, String parentsTags, String defaultLang) {
        super(UNMARKED_CONTENT_TYPE, parentStructureTag, parentsTags);
        this.defaultLang = defaultLang;
        this.operators = operators;
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case CONTENT_ITEM:
                return this.getContentItem();
            default:
                return super.getLinkedObjects(link);
        }
    }

    private List<SEContentItem> getContentItem() {
        if (operators == null) {
            return Collections.emptyList();
        }
        List<SEContentItem> list = new ArrayList<>();
        for (Operator operator : operators) {
            String type = operator.getObjectType();
            if (GFOp_Tj.OP_TJ_TYPE.equals(type) || GFOp_TJ_Big.OP_TJ_BIG_TYPE.equals(type)) {
                list.add(new GFSETextItem((GFOpTextShow)operator, parentStructureTag, parentsTags, defaultLang));
            } else if (operator instanceof GFOp_sh) {
                list.add(new GFSEShadingItem((GFOp_sh)operator, parentStructureTag, parentsTags));
            } else if (operator instanceof GFOpPathPaint && !(operator instanceof GFOp_n)) {
                list.add(new GFSELineArtItem((GFOpPathPaint)operator, parentStructureTag, parentsTags));
            } else if (operator instanceof GFOp_EI) {
                list.add(new GFSEImageItem((GFOp_EI)operator, parentStructureTag, parentsTags));
            } else if (operator instanceof GFOp_Do) {
                List<PDXObject> xObjects = ((GFOp_Do)operator).getXObject();
                if (xObjects != null && xObjects.size() != 0 && ASAtom.IMAGE.getValue().equals(xObjects.get(0).getSubtype())) {
                    list.add(new GFSEImageItem((GFOp_Do)operator, parentStructureTag, parentsTags));
                }
            }
        }
        return Collections.unmodifiableList(list);
    }

}