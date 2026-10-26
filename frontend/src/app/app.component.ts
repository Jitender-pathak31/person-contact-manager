import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Person } from './person.model';
import { PersonService } from './person.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private readonly personService = inject(PersonService);

  readonly people = signal<Person[]>([]);
  readonly loading = signal<boolean>(false);
  readonly errorMessage = signal<string>('');

  searchQuery = '';
  editingId: number | null = null;
  form: Person = this.emptyForm();

  ngOnInit(): void {
    this.loadPeople();
  }

  loadPeople(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.personService.getAll(this.searchQuery).subscribe({
      next: (data) => {
        this.people.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not connect to Spring Boot backend at http://localhost:8080.');
        this.loading.set(false);
      }
    });
  }

  savePerson(): void {
    if (!this.form.firstName.trim() || !this.form.lastName.trim()) {
      this.errorMessage.set('First name and last name are required.');
      return;
    }

    const request$ = this.editingId !== null
      ? this.personService.update(this.editingId, this.form)
      : this.personService.create(this.form);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.loadPeople();
      },
      error: () => this.errorMessage.set('Failed to save contact.')
    });
  }

  startEdit(person: Person): void {
    this.editingId = person.id ?? null;
    this.form = {
      firstName: person.firstName,
      lastName: person.lastName,
      email: person.email ?? '',
      phone: person.phone ?? ''
    };
  }

  deletePerson(id?: number): void {
    if (id === undefined) return;
    this.personService.delete(id).subscribe({
      next: () => {
        if (this.editingId === id) {
          this.resetForm();
        }
        this.loadPeople();
      },
      error: () => this.errorMessage.set('Failed to delete contact.')
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form = this.emptyForm();
    this.errorMessage.set('');
  }

  private emptyForm(): Person {
    return { firstName: '', lastName: '', email: '', phone: '' };
  }
}

