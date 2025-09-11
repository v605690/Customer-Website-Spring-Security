package com.crus.customerWebsite.controllers;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RestController
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private JobExplorer jobExplorer;

//    @GetMapping(value = "/job")
//    public String testJob(@RequestParam(name = "id") String jobId) {
//
//        JobParametersBuilder jobParametersBuilder =
//                new JobParametersBuilder();
//
//        if (StringUtils.hasLength(jobId)) {
//            jobParametersBuilder.addString("jobId", jobId);
//        }
//        JobExecution jobExecution;
//        try {
//            jobExecution =
//                    jobLauncher.run(
//                            job,
//                            jobParametersBuilder.toJobParameters()
//                    );
//        } catch (JobExecutionAlreadyRunningException
//                 | JobRestartException
//                 | JobInstanceAlreadyCompleteException
//                 | JobParametersInvalidException e) {
//            e.printStackTrace();
//            // return exception message
//            return e.getMessage();
//        }
//        // return job execution status
//        return jobExecution.getStatus().name();
//    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("message", "Click the button to start the job.");

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, 1000);
        model.addAttribute("jobInstances", jobInstances);

        List<JobExecution> lastJobExecutions = jobInstances.stream()
                .map(jobExplorer::getLastJobExecution)
                .toList();
        model.addAttribute("lastJobExecutions", lastJobExecutions);

        return "dashboard";
    }

    @PostMapping("/start-job")
    public String startJob(Model model, @RequestParam(name = "jobId", required = false) String jobId) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        if (StringUtils.hasLength(jobId)) {
            jobParametersBuilder.addString("jobId", jobId);
        }
        try {
        JobExecution jobExecution = jobLauncher.run(
                            job,
                            jobParametersBuilder.toJobParameters()
                    );
            model.addAttribute("jobStatus", jobExecution.getStatus().toString());
        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());

        }
        return "redirect:/batch";
    }
}

