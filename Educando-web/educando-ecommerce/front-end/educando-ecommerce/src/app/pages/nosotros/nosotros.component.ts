import { Component, ElementRef } from '@angular/core';
import { gsap } from 'gsap';

@Component({
  selector: 'app-nosotros',
  templateUrl: './nosotros.component.html',
  styleUrls: ['./nosotros.component.css']
})
export class NosotrosComponent {

  constructor(private elementRef: ElementRef) {}

  onMouseEnter(event: MouseEvent) {
    const h1Element = (event.target as HTMLElement)?.querySelector('h1');
    if (h1Element) {
      const initialValue = 0;
      const finalValue = 1234;
      const duration = 500; // Duración de la animación en milisegundos
      this.animateNumber(h1Element, initialValue, finalValue, duration);
    }
  }

  onMouseLeave(event: MouseEvent) {
    
  }

  animateNumber(element: HTMLElement, from: number, to: number, duration: number) {
    const interval = 50; // Intervalo de tiempo entre incrementos (en milisegundos)
    const steps = Math.ceil(duration / interval);
    const increment = (to - from) / steps;

    let currentValue = from;
    let step = 0;

    const updateValue = () => {
      if (step < steps) {
        currentValue += increment;
        element.textContent = Math.round(currentValue).toString();
        step++;
        setTimeout(updateValue, interval);
      } else {
        // Asegúrate de establecer el valor final después de la animación
        element.textContent = to.toString();
      }
    };

    updateValue();
  }

}
