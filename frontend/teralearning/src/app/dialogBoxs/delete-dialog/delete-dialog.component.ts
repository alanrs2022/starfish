import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { QuizService } from 'src/app/service/quiz.service';
import { DialogData } from 'src/model/dialog.model';
import { DeleteMessageDialogComponent } from '../delete-message-dialog/delete-message-dialog.component';
import { DialogRef } from '@angular/cdk/dialog';



@Component({
  selector: 'app-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss']
})
export class DeleteDialogComponent implements OnInit {

  message!:string 
  funId!:number

  constructor(public dialogRef: MatDialogRef<DeleteDialogComponent>,private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: DialogData, private quizService: QuizService,) { }

  ngOnInit(): void {
   
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  onYesClick(qn: number) {
    this.quizService.deleteQuiz(this.data.id).subscribe({
      next: (data: any) => {
        this.dialog.open(DeleteMessageDialogComponent, { data: { message: "Successfully deleted" } })
        this.dialogRef.close();
      },
      error: (e: any) => console.error(e)
    });
    
  }  
}
