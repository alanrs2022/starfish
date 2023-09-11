import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FormGroup,FormControl, FormArray, FormBuilder, Validators } from '@angular/forms';
import { Question } from '../models/question.model';
import { BehaviorSubject } from 'rxjs';
import { QuestionType } from '../models/questionDetailEnum';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private baseURL:string = "http://localhost:8080/api/quiz/"
  private defaultQuestion: Question = {} as Question;
  private questionSource = new BehaviorSubject <Question>(this.defaultQuestion);
  questionData$ = this.questionSource.asObservable();
  sendQuestion(newQuestion: Question){
    this.questionSource.next(newQuestion);
  }
  constructor(private http:HttpClient) { }

  defaultQuestionType:string = QuestionType.singleAnswer; 

  questionForm: FormGroup = new FormGroup({
    $key: new FormControl(null),
    questionType: new FormControl('',Validators.required),
    questionText: new FormControl('',Validators.required),
    answer: new FormControl('',Validators.required),
    mcqOptions: new FormArray([]),
    msqOptions: new FormArray([]),
    answerExplanation: new FormControl('',Validators.required),
  });


  initializeFormGroup() {
    this.questionForm.setValue({
      $key: null,
      questionType: this.defaultQuestionType,
      answerExplanation:'',
      isSingleAnswer: false
    });
  }

  addNewQuestion(newQuestion: any): Observable<any>{
    return this.http.post(this.baseURL+"addQuestion",newQuestion);
  }


}
