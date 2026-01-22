//
//  MeViewModelWrapper.swift
//  iosApp
//
//  Created by Ivan Garza Bermea on 12/4/25.
//
import Swift
import Shared


@MainActor
class MeViewModelWrapper: ObservableObject {
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    @Published var profile: Shared.UserProfile? = nil
    @Published var statistics: Shared.UserStatistics? = nil
    @Published var currentlyReading: [Shared.CurrentlyReadingBook] = []
    @Published var showLogoutConfirmation: Bool = false

    private let helper: MeViewModelHelper
    private var cancellables: [Shared.Closeable] = []

    init() {
        self.helper = MeViewModelHelper()
        startObserving()
    }

    private func startObserving() {
        let stateCancellable = helper.observeState { [weak self] state in
            DispatchQueue.main.async {
                self?.isLoading = state.isLoading
                self?.error = state.error
                self?.profile = state.profile
                self?.statistics = state.statistics
                self?.currentlyReading = state.currentlyReading
                self?.showLogoutConfirmation = state.showLogoutConfirmation
            }
        }
        cancellables.append(stateCancellable)
    }

    func loadUserData(userId: String) {
        helper.loadUserData(userId: userId)
    }

    func refresh() {
        helper.refresh()
    }

    func onSignOutClicked() {
        helper.showLogoutConfirmation()
    }

    func onSignOutDialogDismissed() {
        helper.hideLogoutConfirmation()
    }

    func onSignOutDialogConfirmed() {
        helper.confirmLogout()
    }

    deinit {
        cancellables.forEach { $0.close() }
    }
}
