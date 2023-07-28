import SwiftUI
import UIKit
import MessageUI

public struct EmailView: UIViewControllerRepresentable {

	@Environment(\.presentationMode) var presentation

	let content: Content
	let onCompletion: ((Result<MFMailComposeResult, Error>) -> Void)?

	public init(
		content: Content,
		onCompletion: ((Result<MFMailComposeResult, Error>) -> Void)? = nil
	) {
		self.content = content
		self.onCompletion = onCompletion
	}

	public class Coordinator: NSObject, MFMailComposeViewControllerDelegate {

		@Binding var presentation: PresentationMode
		let onCompletion: ((Result<MFMailComposeResult, Error>) -> Void)?

		init(
			presentation: Binding<PresentationMode>,
			onCompletion: ((Result<MFMailComposeResult, Error>) -> Void)?
		) {
			_presentation = presentation
			self.onCompletion = onCompletion
		}

		public func mailComposeController(
			_ controller: MFMailComposeViewController,
			didFinishWith result: MFMailComposeResult,
			error: Error?
		) {
			defer {
				$presentation.wrappedValue.dismiss()
			}

			guard let onCompletion else { return }

			if let error {
				onCompletion(.failure(error))
			} else {
				onCompletion(.success(result))
			}
		}
	}

	public func makeCoordinator() -> Coordinator {
		Coordinator(presentation: presentation, onCompletion: onCompletion)
	}

	public func makeUIViewController(
		context: UIViewControllerRepresentableContext<EmailView>
	) -> MFMailComposeViewController {
		let vc = MFMailComposeViewController()
		vc.mailComposeDelegate = context.coordinator
		if let recipients = content.recipients {
			vc.setToRecipients(recipients)
		}
		if let subject = content.subject {
			vc.setSubject(subject)
		}
		return vc
	}

	public func updateUIViewController(
		_ uiViewController: MFMailComposeViewController,
		context: UIViewControllerRepresentableContext<EmailView>
	) {
	}
}

extension EmailView {
	public struct Content {
		let recipients: [String]?
		let subject: String?

		public init(
			recipients: [String]?,
			subject: String? = nil
		) {
			self.recipients = recipients
			self.subject = subject
		}
	}
}
