package fudan.ss.mindmapbackend.repository;

import fudan.ss.mindmapbackend.model.AssignmentJudgment;
import fudan.ss.mindmapbackend.model.AssignmentMultiple;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentJudgmentRepository extends Neo4jRepository<AssignmentJudgment, Long> {
    @Query("MATCH (n:Assignment_judgment) WHERE n.judge_id = ({judge_id}) RETURN n")
    List<AssignmentJudgment> findByJudge_id(@Param("judge_id") String judge_id);

    @Query("start  n = node({id}) " +
            "MATCH (:Node)-[r:HAS_ASSIGNMENT_JUDGMENT]->(n) DELETE r")
    void deleteFather(@Param("id") Long id);

    @Query("start  n = node({id}) " +
            "MATCH (m:Node) WHERE m.course_mindmap = ({course_mindmap}) and m.node_id=({node_id}) " +
            "CREATE (m)-[:HAS_ASSIGNMENT_JUDGMENT]->(n)")
    void createFather(@Param("id") Long id, @Param("course_mindmap") String course_mindmap, @Param("node_id") String node_id);

}