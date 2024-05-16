import { TestBed } from '@angular/core/testing';

import { MailChimpService } from './mail-chimp.service';

describe('MailChimpService', () => {
  let service: MailChimpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MailChimpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
