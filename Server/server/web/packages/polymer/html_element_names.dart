// Copyright (c) 2012, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.
library polymer.html_element_names;

/**
 * HTML element to DOM type mapping. Source:
 * <http://dev.w3.org/html5/spec/section-index.html#element-interfaces>
 *
 * The 'HTML' prefix has been removed to match `dart:html`, as per:
 * <http://code.google.com/p/dart/source/browse/branches/bleeding_edge/dart/lib/html/scripts/htmlrenamer.py>
 * It does not appear any element types are being renamed other than the prefix.
 * However there does not appear to be the last subtypes for the following tags:
 * command, data, td, th, and time.
 */
const HTML_ELEMENT_NAMES = const {
    'a': 'AnchorElement',
    'abbr': 'Element',
    'address': 'Element',
    'area': 'AreaElement',
    'article': 'Element',
    'aside': 'Element',
    'audio': 'AudioElement',
    'b': 'Element',
    'base': 'BaseElement',
    'bdi': 'Element',
    'bdo': 'Element',
    'blockquote': 'QuoteElement',
    'body': 'BodyElement',
    'br': 'BRElement',
    'button': 'ButtonElement',
    'canvas': 'CanvasElement',
    'caption': 'TableCaptionElement',
    'cite': 'Element',
    'code': 'Element',
    'col': 'TableColElement',
    'colgroup': 'TableColElement',
    'command': 'Element', // see doc comment, was: 'CommandElement'
    'data': 'Element', // see doc comment, was: 'DataElement'
    'datalist': 'DataListElement',
    'dd': 'Element',
    'del': 'ModElement',
    'details': 'DetailsElement',
    'dfn': 'Element',
    'dialog': 'DialogElement',
    'div': 'DivElement',
    'dl': 'DListElement',
    'dt': 'Element',
    'em': 'Element',
    'embed': 'EmbedElement',
    'fieldset': 'FieldSetElement',
    'figcaption': 'Element',
    'figure': 'Element',
    'footer': 'Element',
    'form': 'FormElement',
    'h1': 'HeadingElement',
    'h2': 'HeadingElement',
    'h3': 'HeadingElement',
    'h4': 'HeadingElement',
    'h5': 'HeadingElement',
    'h6': 'HeadingElement',
    'head': 'HeadElement',
    'header': 'Element',
    'hgroup': 'Element',
    'hr': 'HRElement',
    'html': 'HtmlElement',
    'i': 'Element',
    'iframe': 'IFrameElement',
    'img': 'ImageElement',
    'input': 'InputElement',
    'ins': 'ModElement',
    'kbd': 'Element',
    'keygen': 'KeygenElement',
    'label': 'LabelElement',
    'legend': 'LegendElement',
    'li': 'LIElement',
    'link': 'LinkElement',
    'map': 'MapElement',
    'mark': 'Element',
    'menu': 'MenuElement',
    'meta': 'MetaElement',
    'meter': 'MeterElement',
    'nav': 'Element',
    'noscript': 'Element',
    'object': 'ObjectElement',
    'ol': 'OListElement',
    'optgroup': 'OptGroupElement',
    'option': 'OptionElement',
    'output': 'OutputElement',
    'p': 'ParagraphElement',
    'param': 'ParamElement',
    'pre': 'PreElement',
    'progress': 'ProgressElement',
    'q': 'QuoteElement',
    'rp': 'Element',
    'rt': 'Element',
    'ruby': 'Element',
    's': 'Element',
    'samp': 'Element',
    'script': 'ScriptElement',
    'section': 'Element',
    'select': 'SelectElement',
    'small': 'Element',
    'source': 'SourceElement',
    'span': 'SpanElement',
    'strong': 'Element',
    'style': 'StyleElement',
    'sub': 'Element',
    'summary': 'Element',
    'sup': 'Element',
    'table': 'TableElement',
    'tbody': 'TableSectionElement',
    'td': 'TableCellElement', // see doc comment, was: 'TableDataCellElement'
    'template': 'TemplateElement',
    'textarea': 'TextAreaElement',
    'tfoot': 'TableSectionElement',
    'th': 'TableCellElement', // see doc comment, was: 'TableHeaderCellElement'
    'thead': 'TableSectionElement',
    'time': 'Element', // see doc comment, was: 'TimeElement'
    'title': 'TitleElement',
    'tr': 'TableRowElement',
    'track': 'TrackElement',
    'u': 'Element',
    'ul': 'UListElement',
    'var': 'Element',
    'video': 'VideoElement',
    'wbr': 'Element',
};
