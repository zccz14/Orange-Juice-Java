package com.funcxy.oj.controllers;

import com.funcxy.oj.models.Problem;
import com.funcxy.oj.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtupc96 on 2017/2/28.
 */

@RestController
@RequestMapping("/problems")
public class ProblemController {
    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.POST)
    public Problem saveProblem(@Valid Problem problem) {
        problem.setTitle(problem.getTitle());
        problem.setDescription(problem.getDescription());
        if (problem.getReferenceAnswer() != null)
            problem.setReferenceAnswer(problem.getReferenceAnswer());
        problem.setType(problem.getType());
        return problemRepository.save(problem);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Problem> getProblem(Problem problem){
        List<Problem> problemList = new ArrayList<>();
        if (problem.getType() != null)
            problemList.addAll(problemRepository.findByType(problem.getType()));
        if (problem.getTitle() != null)
            if (problemList.isEmpty())
                problemList.addAll(problemRepository.findByTitle(problem.getTitle()));
            else
                problemList.retainAll(problemRepository.findByTitle(problem.getTitle()));
        if (problem.getCreator() != null)
            if (problemList.isEmpty())
                problemList.addAll(problemRepository.findByCreator(problem.getCreator()));
            else
                problemList.addAll(problemRepository.findByCreator(problem.getCreator()));
        return problemList;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Problem updateProblem(Problem problem){
        return problemRepository.save(problem);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public Problem deleteProblem(Problem problem){
        Problem tempProblem = problemRepository.findById(problem.getId());
        problemRepository.delete(problem);
        return tempProblem;
    }
}
