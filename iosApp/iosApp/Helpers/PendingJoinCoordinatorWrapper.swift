//
//  PendingJoinCoordinatorWrapper.swift
//  iosApp
//
//  iOS wrapper for shared PendingJoinCoordinator
//
import Swift
import Shared

enum AutoJoinResultWrapper: Equatable {
    case success(clubId: String)
    case failure(message: String?)

    static func from(_ kotlinResult: Shared.AutoJoinResult) -> AutoJoinResultWrapper {
        if let success = kotlinResult as? Shared.AutoJoinResultSuccess {
            return .success(clubId: success.clubId)
        } else if let failure = kotlinResult as? Shared.AutoJoinResultFailure {
            return .failure(message: failure.message)
        }
        return .failure(message: nil)
    }
}

@MainActor
class PendingJoinCoordinatorWrapper: ObservableObject {
    /// One-shot signal — the view observing this should consume it (set back to nil)
    /// once handled, mirroring the one-shot event fields on other ViewModel states.
    @Published var autoJoinResult: AutoJoinResultWrapper? = nil

    /// Token from an invite Universal Link awaiting navigation to the Join screen.
    /// Consumed via `onConsumeIncomingInviteToken()` once the view has routed.
    @Published var incomingInviteToken: String? = nil

    private let helper: PendingJoinCoordinatorHelper
    private var cancellable: Shared.Closeable?
    private var inviteCancellable: Shared.Closeable?

    init() {
        self.helper = PendingJoinCoordinatorHelper()
        startObserving()
    }

    private func startObserving() {
        cancellable = helper.observeAutoJoinResult { [weak self] result in
            Task { @MainActor [weak self] in
                guard let self else { return }
                self.autoJoinResult = AutoJoinResultWrapper.from(result)
            }
        }
        inviteCancellable = helper.observeIncomingInviteToken { [weak self] token in
            Task { @MainActor [weak self] in
                guard let self else { return }
                self.incomingInviteToken = token
            }
        }
    }

    func setPendingToken(_ token: String) { helper.setPendingToken(token: token) }
    func onConsumeAutoJoinResult() { autoJoinResult = nil }

    /// Returns true when `url` was a recognized invite link, so the caller can fall through
    /// to other URL handlers when it wasn't.
    @discardableResult
    func onInviteLinkOpened(_ url: URL) -> Bool {
        helper.onInviteLinkOpened(url: url.absoluteString)
    }

    func onConsumeIncomingInviteToken() {
        helper.onConsumeIncomingInviteToken()
        incomingInviteToken = nil
    }

    deinit {
        cancellable?.close()
        inviteCancellable?.close()
    }
}
