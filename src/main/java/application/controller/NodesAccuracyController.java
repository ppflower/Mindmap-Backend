package application.controller;

import application.controller.json_model.NodeValue;
import application.controller.json_model.NodesAccuracy;
import application.model.*;
import application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;

@RestController
@CrossOrigin
public class NodesAccuracyController {
    @Autowired
    private MindmapService mindmapService;
    @Autowired
    private NodeService nodeService;

    @RequestMapping(value = "/nodes_accuracy_mul/{mindmap_id}", method = RequestMethod.GET)
    public List<NodesAccuracy> nodesAccuracyOfMul(@PathVariable String mindmap_id) {
        return getAccuracy(mindmap_id, "Multiple");
    }

    @RequestMapping(value = "/nodes_accuracy_jud/{mindmap_id}", method = RequestMethod.GET)
    public List<NodesAccuracy> nodesAccuracyOfJud(@PathVariable String mindmap_id) {
        return getAccuracy(mindmap_id, "Judgment");
    }

    @RequestMapping(value = "/nodes_value/{mindmap_id}", method = RequestMethod.GET)
    public List<NodeValue> nodesValue(@PathVariable String mindmap_id) {
        return getNodeValue(mindmap_id);
    }

    private List<NodesAccuracy> getAccuracy(String mindmap_id, String type) {
        List<NodesAccuracy> nodesAccuracyList = new LinkedList<>();

        //获得mindmap
        Mindmap tempMindmap = mindmapService.findByMindmapId(mindmap_id);

        Node root_node = mindmapService.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodesAccuracyList;

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            //获得答题人数
            int number = 0;
            int correctNumber = 0;

            if (type.equals("Multiple")) {
                AssignmentMultiple[] multiples = nodeService.findAssignmentMultiple(thisNode.getLong_id());
                for (AssignmentMultiple mul : multiples) {
                    number += Integer.parseInt(mul.getNumber());
                    correctNumber += Integer.parseInt(mul.getCorrect_number());
                }
            } else if (type.equals("Judgment")) {
                AssignmentJudgment[] judgments = nodeService.findAssignmentJudgements(thisNode.getLong_id());
                for (AssignmentJudgment judgment : judgments) {
                    number += Integer.parseInt(judgment.getNumber());
                    correctNumber += Integer.parseInt(judgment.getCorrect_number());
                }
            }


            //加入到nodesAccuracyList中
            NodesAccuracy nodesAccuracy = new NodesAccuracy();
            nodesAccuracy.setNode_id(thisNode.getId());
            nodesAccuracy.setNode_topic(thisNode.getTopic());
            nodesAccuracy.setNumber(number + "");
            nodesAccuracy.setCorrect_number(correctNumber + "");

            String acc = "0.00";
            DecimalFormat df = new DecimalFormat("#.00");
            if (number != 0)
                acc = df.format((double) correctNumber / number);
            nodesAccuracy.setAccuracy(acc);
            nodesAccuracyList.add(nodesAccuracy);

            for (Node child : nodeService.findChildren(thisNode.getLong_id())) {
                nodesChildren.add(child);
            }
            //移除
            nodes.remove();
        }

        return nodesAccuracyList;
    }

    private List<NodeValue> getNodeValue(String mindmap_id) {
        List<NodeValue> nodeValueList = new ArrayList<>();
        //获得mindmap
        Mindmap tempMindmap = mindmapService.findByMindmapId(mindmap_id);

        Node root_node = mindmapService.findRootNode(tempMindmap.getId());
        if (root_node == null)
            return nodeValueList;

        //深度遍历
        Queue<Node> nodes = new LinkedList<>();
        Queue<Node> nodesChildren = new LinkedList<>();
        nodes.add(root_node);

        while (!nodes.isEmpty() || !nodesChildren.isEmpty()) {

            if (nodes.isEmpty()) {
                nodes = nodesChildren;
                nodesChildren = new LinkedList<>();
            }
            Node thisNode = nodes.peek();

            //获得答题人数
            int number = 0;
            int correctNumber = 0;

            int tmpScore = 0;
            int tmpStudentScore = 0;

            // 每个node创建一个nodeValue
            NodeValue nodeValue = new NodeValue();
            nodeValue.setNode_id(thisNode.getId());
            nodeValue.setNode_topic(thisNode.getTopic());


            // 选择题回答情况统计
            AssignmentMultiple[] multiples = nodeService.findAssignmentMultiple(thisNode.getLong_id());
            for (AssignmentMultiple mul : multiples) {
                number = Integer.parseInt(mul.getNumber());
                correctNumber = Integer.parseInt(mul.getCorrect_number());

                tmpScore += number * mul.getValue();
                tmpStudentScore += correctNumber * mul.getValue();
            }

            // 判断题回答情况统计
            AssignmentJudgment[] judgments = nodeService.findAssignmentJudgements(thisNode.getLong_id());
            for (AssignmentJudgment judgment : judgments) {
                number = Integer.parseInt(judgment.getNumber());
                correctNumber = Integer.parseInt(judgment.getCorrect_number());

                tmpScore += number * judgment.getValue();
                tmpStudentScore += correctNumber * judgment.getValue();
            }

            nodeValue.setScore(tmpScore);
            nodeValue.setStudentScore(tmpStudentScore);
            nodeValue.setValue(tmpScore == 0 ? 0.0 : (double)tmpStudentScore/tmpScore);

            //加入到nodesValueList中
            nodeValueList.add(nodeValue);

            Collections.addAll(nodesChildren, nodeService.findChildren(thisNode.getLong_id()));
//
//            for (Node child : nodeService.findChildren(thisNode.getLong_id())) {
//                nodesChildren.add(child);
//            }
//
            //移除
            nodes.remove();
        }

        return nodeValueList;
    }
}
