package com.cognizant.service;
import java.util.List;
import java.util.Optional;
//import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.Optional;

//import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import com.cognizant.Exception.DuplicateISBNException;
import com.cognizant.dto.BookDTO;
import com.cognizant.entity.Book;
import com.cognizant.repository.BookManagementModuleRepo;

import jakarta.validation.Valid;

@Service
public class BookManagementModuleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BookManagementModuleService.class);
	
	@Autowired
	private BookManagementModuleRepo bookManagementModuleRepo;
	
	
//	@Transactional
	public BookDTO addBook(@Valid BookDTO bookDTO) {
		LOGGER.info("Adding a new book");
	
//		-----------------------------------------------------------------------------------------------------
		if(bookDTO.getIsbn() != null) {
			Optional<Book> existingBook = bookManagementModuleRepo.findByIsbn(bookDTO.getIsbn());
			if(existingBook.isPresent()) {
				throw new DuplicateISBNException("Book with ISBN " + bookDTO.getIsbn() + " alraedy exist.");
			}
		}
//		-----------------------------------------------------------------------------------------------------
		
		Book book = convertToEntity(bookDTO);
		Book savedBook = bookManagementModuleRepo.save(book); 
		LOGGER.debug("Book saved with ID: {}", savedBook.getBookId());
		return convertToDTO(savedBook);
	}
	
	public BookDTO updateBook(Long id, BookDTO bookDTO) {
		// TODO Auto-generated method stub
		LOGGER.info("Updating a book with id: {}",id);
		/* Exception still need to write  */
		Book book = bookManagementModuleRepo.findById(id).orElseThrow(()->{
			LOGGER.error("Book with ID {} not found", id);
			return new RuntimeException("Book NOt Found");
		});
		
		
		
//		if(book.getIsbn() !=null && book.getIsbn().equals(book.getIsbn()) ){
//			Optional<Book> existingBook = bookManagementModuleRepo.findByIsbn(book.getIsbn());
//			if(existingBook.isPresent()) {
//				/*  --- Still need to work in here U_U --- */
//				System.out.println("Book already exist");
//			}
//		}
		
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		book.setGenre(bookDTO.getGenre());
		book.setIsbn(bookDTO.getIsbn());
		book.setYearPublished(bookDTO.getYearPublished());
		book.setAvailableCopies(bookDTO.getAvailableCopies());
		
		Book updatedBook = bookManagementModuleRepo.save(book);
		LOGGER.debug("Updated book: {}", updatedBook);
		return convertToDTO(updatedBook);
	}
	
	
	public List<BookDTO> getAllBooks() {
		// TODO Auto-generated method stub
		LOGGER.info("Fetching all available books");
		return bookManagementModuleRepo.findByAvailableCopiesGreaterThan(0).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public BookDTO getBookById(Long id) {
		// TODO Auto-generated method stub
		/* Exception */
		LOGGER.info("Fetching book with ID: {}", id);
		Book book = bookManagementModuleRepo.findById(id).orElseThrow(
				()->{
					LOGGER.error("Book with Id {} not found", id);
					return new RuntimeException("Book not Found");
				});
		return convertToDTO(book); 
	}
	
	public void deleteBookById(Long id) {
		// TODO Auto-generated method stub
		if(!bookManagementModuleRepo.existsById(id)) {
//			System.out.println("Later on...i will handle. 😒");
			LOGGER.warn("Book with ID {} dos not exist", id);
		}
		LOGGER.info("Deleting book with ID: {}",id);
		bookManagementModuleRepo.deleteById(id);
	}

	public void deleteAllBook() {
		// TODO Auto-generated method stub
		LOGGER.warn("Deleting all books from database😢.");
		bookManagementModuleRepo.deleteAll();
	}
	
	public List<BookDTO> searchBookByTitle(String title) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching book by title: {}", title);
		return bookManagementModuleRepo.findByTitleContainingIgnoreCase(title)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public List<BookDTO> searchBookByAuthor(String author) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching book by author: {}", author);
		return bookManagementModuleRepo.findByAuthorContainingIgnoreCase(author)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public List<BookDTO> searchBookByGenre(String genre) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching book by genre: {}", genre);
		return bookManagementModuleRepo.findByGenreContainingIgnoreCase(genre)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public BookDTO searchBookByIsbn(String isbn) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching book by ISBN: {}", isbn);
		Book book = bookManagementModuleRepo.findByIsbn(isbn).orElseThrow(
				()->{
					LOGGER.error("Book with ISBN {} not found", isbn);
					return new RuntimeException("Book Not Found");
				});
		return convertToDTO(book);
	}	
	
	public List<BookDTO> searchBookByYear(Integer year) {
		// TODO Auto-generated method stub 
		LOGGER.info("Searching book by year: {}", year);
		return bookManagementModuleRepo.findByYearPublished(year)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public List<BookDTO> searchBookByYearRange(Integer startYear, Integer endYear) {
		// TODO Auto-generated method stub
		LOGGER.info("Searching book published between {} and {}", startYear, endYear);
		return bookManagementModuleRepo.searchBookByYearRange(startYear, endYear)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	public List<BookDTO> uploadBooks(@Valid List<BookDTO> bookList) {
		// TODO Auto-generated method stub
		LOGGER.info("Bulk upload of {} books", bookList.size());
		List<Book> books = bookList.stream()
				.map(this::convertToEntity).collect(Collectors.toList());
		List<Book> savedBooks = bookManagementModuleRepo.saveAll(books);
		return convertToDTO(savedBooks);
	}
	
	
	
	/*    JUST TO CONVERT THINGS...SO CALLED MODEL MAPPING    */
	
	private BookDTO convertToDTO(Book book) {
	    BookDTO dto = new BookDTO();
	    dto.setBookId(book.getBookId());
	    dto.setTitle(book.getTitle());
	    dto.setAuthor(book.getAuthor());
	    dto.setGenre(book.getGenre());
	    dto.setIsbn(book.getIsbn());
	    dto.setYearPublished(book.getYearPublished());
	    dto.setAvailableCopies(book.getAvailableCopies());
	    return dto;
	}

	private Book convertToEntity(BookDTO dto) {
	    Book book = new Book();
	    book.setBookId(dto.getBookId());
	    book.setTitle(dto.getTitle());
	    book.setAuthor(dto.getAuthor());
	    book.setGenre(dto.getGenre());
	    book.setIsbn(dto.getIsbn());
	    book.setYearPublished(dto.getYearPublished());
	    book.setAvailableCopies(dto.getAvailableCopies());
	    return book;
	}
	
	private List<BookDTO> convertToDTO(List<Book> books) {
	    return books.stream()
	            .map(this::convertToDTO)
	            .collect(Collectors.toList());
	}


	

	
//	private BookDTO convertToDTO(Book book) {
//		// TODO Auto-generated method stub
//		BookDTO bookDTO = new BookDTO();
//		BeanUtils.copyProperties(book, bookDTO);
//		return bookDTO;
//	}
//
//	private Book convertToEntity(@Valid BookDTO bookDTO) {
//		// TODO Auto-generated method stub
//		Book book = new Book();
//		BeanUtils.copyProperties(bookDTO, book);
//		return book;
//	}
}
